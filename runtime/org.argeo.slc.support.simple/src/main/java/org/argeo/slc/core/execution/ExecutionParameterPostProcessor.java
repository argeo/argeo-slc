package org.argeo.slc.core.execution;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.execution.ExecutionContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.ManagedSet;
import org.springframework.util.ObjectUtils;

public class ExecutionParameterPostProcessor extends
		InstantiationAwareBeanPostProcessorAdapter {

	private final static Log log = LogFactory
			.getLog(ExecutionParameterPostProcessor.class);

//	private CustomPpc ppc = new CustomPpc(new Properties());
	
	private ExecutionContext executionContext;

	private ExecutionScope executionScope;

	private InstantiationManager instantiationManager;

	public InstantiationManager getInstantiationManager() {
		return instantiationManager;
	}

	public void setInstantiationManager(
			InstantiationManager instantiationManager) {
		this.instantiationManager = instantiationManager;
	}

	public void setExecutionScope(ExecutionScope executionScope) {
		this.executionScope = executionScope;
	}

	public ExecutionContext getExecutionContext() {
		return executionContext;
	}

	public void setExecutionContext(ExecutionContext executionContext) {
		this.executionContext = executionContext;
	}

	private String placeholderPrefix = "@{";
	private String placeholderSuffix = "}";
	private String nullValue;

	protected Object resolveValue(Object value, CustomPpc ppc) {		
		if (value instanceof TypedStringValue) {
			TypedStringValue tsv = (TypedStringValue) value;
			return ppc.process(tsv.getValue());
		}
		else if (value instanceof String) {
			return ppc.process(value.toString());
		}		
		else if (value instanceof Map) {
			Map mapVal = (Map) value;
			
			Map newContent = new LinkedHashMap();
			boolean entriesModified = false;
			for (Iterator it = mapVal.entrySet().iterator(); it.hasNext();) {
				Map.Entry entry = (Map.Entry) it.next();
				Object key = entry.getKey();
				int keyHash = (key != null ? key.hashCode() : 0);
				Object newKey = resolveValue(key,ppc);
				int newKeyHash = (newKey != null ? newKey.hashCode() : 0);
				Object val = entry.getValue();
				Object newVal = resolveValue(val,ppc);
				newContent.put(newKey, newVal);
				entriesModified = entriesModified || (newVal != val || newKey != key || newKeyHash != keyHash);
			}
			if (entriesModified) {
				mapVal.clear();
				mapVal.putAll(newContent);
			}
			return mapVal;
		}
		else if (value instanceof List) {
			List listVal = (List) value;
			for (int i = 0; i < listVal.size(); i++) {
				Object elem = listVal.get(i);
				Object newVal = resolveValue(elem,ppc);
				if (!ObjectUtils.nullSafeEquals(newVal, elem)) {
					listVal.set(i, newVal);
				}
			}			
			return value;
		}
		else if (value instanceof Set) {
			Set setVal = (Set) value;
			Set newContent = new LinkedHashSet();
			boolean entriesModified = false;
			for (Iterator it = setVal.iterator(); it.hasNext();) {
				Object elem = it.next();
				int elemHash = (elem != null ? elem.hashCode() : 0);
				Object newVal = resolveValue(elem,ppc);
				int newValHash = (newVal != null ? newVal.hashCode() : 0);
				newContent.add(newVal);
				entriesModified = entriesModified || (newVal != elem || newValHash != elemHash);
			}
			if (entriesModified) {
				setVal.clear();
				setVal.addAll(newContent);
			}	
			return value;
		}
		else {
			return value;
		}
	}
	
	@Override
	public PropertyValues postProcessPropertyValues(PropertyValues pvs,
			PropertyDescriptor[] pds, Object bean, String beanName)
			throws BeansException {

		//TODO: resolve at execution only if scope is execution
		
//		boolean inFlowInitialization = instantiationManager
//				.isInFlowInitialization();
//
//		if (((executionScope == null) || (!executionScope.hasExecutionContext()))
//				&& !inFlowInitialization) {
//			// log.info("Skip parameter conversion for bean " + beanName);
//			return pvs;
//		} else {
//			// log.info("Execute parameter conversion for bean " + beanName);
//		}

		// copy the property values
		//MutablePropertyValues newPv = new MutablePropertyValues(pvs);
		
		Properties props = new Properties();
		CustomPpc ppc = new CustomPpc(props);

		for (PropertyValue pv : pvs.getPropertyValues()) {
			// log.info("   PropertyValue pv " + pv.getValue() + " - "
			// + pv.getValue().getClass());
			String originalValue = null;
			String convertedValue = null;
						
			if (pv.getValue() instanceof TypedStringValue) {
				TypedStringValue tsv = (TypedStringValue) pv.getValue();
				originalValue = tsv.getValue();
				convertedValue = ppc.process(originalValue);
				if (!convertedValue.equals(originalValue)) 
					tsv.setValue(convertedValue);
			}
			else if (pv.getValue() instanceof String) {
				originalValue = pv.getValue().toString();
				convertedValue = ppc.process(originalValue);
				// Setting the convertedValue can be problematic since
				// calling setConvertedValue also sets a flag setConvertedValue
				if (!convertedValue.equals(originalValue))
					pv.setConvertedValue(convertedValue);
			}	
			
			else if ((pv.getValue() instanceof ManagedMap)
					||(pv.getValue() instanceof ManagedList)
					||(pv.getValue() instanceof ManagedSet)){
				resolveValue(pv.getValue(),ppc);			
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

		/** Public access to the internals of PropertyPlaceholderConfigurer*/
		public String process(String strVal) {
			String value = parseStringValue(strVal, this.props,
					new HashSet<String>());
			return (value.equals(nullValue) ? null : value);
		}

		@Override
		protected String resolvePlaceholder(String placeholder, Properties props) {
			// log.info("Try convert placeholder " + placeholder);
			if ((executionScope != null)
					&& (executionScope.hasExecutionContext()))
				return executionContext.getVariable(placeholder).toString();
			else if (instantiationManager.isInFlowInitialization())
				return instantiationManager.getInitializingFlowParameter(
						placeholder).toString();
			else
				return super.resolvePlaceholder(placeholder, props);
		}
	}
}
