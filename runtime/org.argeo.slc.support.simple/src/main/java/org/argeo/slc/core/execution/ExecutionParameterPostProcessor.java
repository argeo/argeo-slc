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
		if (!ExecutionContext.isExecuting()){
				//&& !DefaultExecutionSpec.isInFlowInitialization()) {
			//log.info("Skip parameter conversion for bean " + beanName);
			return pvs;
		} else {
			//log.info("Execute parameter conversion for bean " + beanName);
		}

		Properties props = new Properties();
		CustomPpc ppc = new CustomPpc(props);

		for (PropertyValue pv : pvs.getPropertyValues()) {
//			log.info("   PropertyValue pv " + pv.getValue() + " - "
//					+ pv.getValue().getClass());
			String originalValue = null;
			String convertedValue = null;
			if (pv.getValue() instanceof TypedStringValue) {
				TypedStringValue tsv = (TypedStringValue) pv.getValue();
				originalValue = tsv.getValue();
				convertedValue = ppc.process(originalValue);
				tsv.setValue(convertedValue);
			} else if (pv.getValue() instanceof String) {
				originalValue = pv.getValue().toString();
				convertedValue = ppc.process(originalValue);
				pv.setConvertedValue(convertedValue);
			}
			if (convertedValue != null && log.isTraceEnabled()) {
				if (!originalValue.equals(convertedValue))
					log.trace("Converted field '" + pv.getName() + "': '"
							+ originalValue + "' to '" + convertedValue
							+ "' in bean " + beanName);
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
			//log.info("Try convert placeholder " + placeholder);
			if (ExecutionContext.isExecuting())
				return ExecutionContext.getVariable(placeholder).toString();
			else if (DefaultExecutionSpec.isInFlowInitialization())
				return DefaultExecutionSpec.getInitializingFlowParameter(
						placeholder).toString();
			else
				return super.resolvePlaceholder(placeholder, props);
		}

	}
}
