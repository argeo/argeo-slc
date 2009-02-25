package org.argeo.slc.core.execution;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.config.TypedStringValue;

public class ExecutionParameterPostProcessor extends
		InstantiationAwareBeanPostProcessorAdapter {
	private final static Log log = LogFactory
			.getLog(ExecutionParameterPostProcessor.class);

	private String placeholderPrefix = "@{";
	private String placeholderSuffix = "}";
	private String nullValue;

	@Override
	public PropertyValues postProcessPropertyValues(PropertyValues pvs,
			PropertyDescriptor[] pds, Object bean, String beanName)
			throws BeansException {
		if (!ExecutionContext.isExecuting())
			return pvs;

//		ExecutionFlow currentFlow = ExecutionContext.getCurrentFlow();
//
//		Properties props = new Properties();
//		Map<String, Object> attributes = currentFlow.getAttributes();
//		Map<String, ExecutionSpecAttribute> specAttributes = currentFlow
//				.getExecutionSpec().getAttributes();
//
//		for (String key : specAttributes.keySet()) {
//			ExecutionSpecAttribute obj = specAttributes.get(key);
//			if (!(obj instanceof RefSpecAttribute)) {
//				if (!attributes.containsKey(key))
//					throw new SlcException("Specified attribute " + key
//							+ " is not set in " + currentFlow);
//
//				props.setProperty(key, attributes.get(key).toString());
//				// if (log.isTraceEnabled())
//				// log.trace("Use attribute " + key);
//			}
//		}

		Properties props = new Properties();
		CustomPpc ppc = new CustomPpc(props);

		for (PropertyValue pv : pvs.getPropertyValues()) {
			if (pv.getValue() instanceof TypedStringValue) {
				TypedStringValue tsv = (TypedStringValue) pv.getValue();
				String originalValue = tsv.getValue();
				String convertedValue = ppc.process(originalValue);
				tsv.setValue(convertedValue);
				if (log.isTraceEnabled()) {
					if (!originalValue.equals(convertedValue))
						log.trace("Converted field '" + pv.getName() + "': '"
								+ originalValue + "' to '" + convertedValue
								+ "' in bean " + beanName);
				}
			} else {
				// if (log.isTraceEnabled())
				// log.trace(beanName + "[" + pv.getName() + "]: "
				// + pv.getValue().getClass());
			}
		}

		return pvs;
	}

	public void setPlaceholderPrefix(String placeholderPrefix) {
		this.placeholderPrefix = placeholderPrefix;
	}

	public void setPlaceholderSuffix(String placeholderSuffix) {
		this.placeholderSuffix = placeholderSuffix;
	}

	public void setNullValue(String nullValue) {
		this.nullValue = nullValue;
	}

	private class CustomPpc extends PropertyPlaceholderConfigurer {
		private final Properties props;

		public CustomPpc(Properties props) {
			super();
			this.props = props;
			setPlaceholderPrefix(placeholderPrefix);
			setPlaceholderSuffix(placeholderSuffix);
			setSystemPropertiesMode(SYSTEM_PROPERTIES_MODE_NEVER);
		}

		public String process(String strVal) {
			String value = parseStringValue(strVal, this.props,
					new HashSet<String>());
			return (value.equals(nullValue) ? null : value);
		}

		@Override
		protected String resolvePlaceholder(String placeholder, Properties props) {
			if (ExecutionContext.isExecuting())
				return ExecutionContext.getVariable(placeholder).toString();
			else if (SimpleExecutionSpec.isInFlowInitialization())
				return SimpleExecutionSpec.getInitializingFlowParameter(
						placeholder).toString();
			else
				return super.resolvePlaceholder(placeholder, props);
		}

	}
}
