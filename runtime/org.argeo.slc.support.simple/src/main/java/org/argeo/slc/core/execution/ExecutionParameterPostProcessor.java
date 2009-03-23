package org.argeo.slc.core.execution;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
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
	
	private ExecutionContext executionContext;

	private ExecutionScope executionScope;

	private InstantiationManager instantiationManager;
	
	private Map<String, PropertyValues> storedPvsMap = new HashMap<String, PropertyValues>();

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

	@Override
	public PropertyValues postProcessPropertyValues(PropertyValues pvs,
			PropertyDescriptor[] pds, Object bean, String beanName)
			throws BeansException {

		//TODO: resolve at execution only if scope is execution
			
		// if 
		PropertyValues sourcePvs = pvs;
		if(storedPvsMap.containsKey(beanName)) {
			sourcePvs = storedPvsMap.get(beanName);
			log.info("Use storedPvsMap for bean " + beanName);
		}
		
		MutablePropertyValues newPvs = new MutablePropertyValues();
		
		boolean changesOccured = false;
				
		CustomPpc ppc = new CustomPpc(beanName);
		
		for(int i=0; i<sourcePvs.getPropertyValues().length; i++) {
			
			PropertyValue pv = pvs.getPropertyValues()[i];
			
			if (pv.getValue() instanceof TypedStringValue) {
				TypedStringValue tsv = (TypedStringValue) pv.getValue();
				String originalValue = tsv.getValue();
				String convertedValue = ppc.resolveString(originalValue);
				// add a new Property value to newPvs, identical to pv
				// except for the value
				newPvs.addPropertyValue(new PropertyValue(pv, new TypedStringValue(convertedValue)));
				if (!convertedValue.equals(originalValue)) {
					changesOccured = true;
				}
			}
			else if (pv.getValue() instanceof String) {
				String originalValue = pv.getValue().toString();			
				String convertedValue = ppc.resolveString(originalValue);
				newPvs.addPropertyValue(new PropertyValue(pv, convertedValue));
				if (!convertedValue.equals(originalValue)) {
					changesOccured = true;
				}
			}		
			else if ((pv.getValue() instanceof ManagedMap)
					||(pv.getValue() instanceof ManagedList)
					||(pv.getValue() instanceof ManagedSet)){

				Object convertedValue = ppc.resolveValue(pv.getValue());
				newPvs.addPropertyValue(new PropertyValue(pv, convertedValue));
				if(convertedValue != pv.getValue()) {
					changesOccured = true;
				}
			}		
			else {
				newPvs.addPropertyValue(new PropertyValue(pv));
			}
		}
		
		if(changesOccured) {
			storedPvsMap.put(beanName, pvs);
			log.info("Add storedPvsMap for Bean " + beanName);
			return newPvs;
		}
		else {
			// no change, return pvs
			return pvs;
		}
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
		String beanName;
		
		public CustomPpc(String beanName) {
			super();
			this.props = new Properties();
			this.beanName = beanName;
			setPlaceholderPrefix(placeholderPrefix);
			setPlaceholderSuffix(placeholderSuffix);
			setSystemPropertiesMode(SYSTEM_PROPERTIES_MODE_NEVER);
		}

		/** Public access to the internals of PropertyPlaceholderConfigurer*/
		public String resolveString(String strVal) {
			String value = parseStringValue(strVal, this.props,
					new HashSet<String>());
			return (value.equals(nullValue) ? null : value);
		}
		
		public Object resolveValue(Object value) {
			if (value instanceof TypedStringValue) {
				TypedStringValue tsv = (TypedStringValue) value;
				String originalValue = tsv.getValue();

				String convertedValue = resolveString(originalValue);
				return convertedValue.equals(originalValue) ? value : new TypedStringValue(convertedValue);
			}
			else if (value instanceof String) {
				String originalValue = value.toString();			
				String convertedValue = resolveString(originalValue);
				return convertedValue.equals(originalValue) ? value : convertedValue;
			}		
			else if (value instanceof Map) {
				Map mapVal = (Map) value;
				
				Map newContent = new ManagedMap();
				boolean entriesModified = false;
				for (Iterator it = mapVal.entrySet().iterator(); it.hasNext();) {
					Map.Entry entry = (Map.Entry) it.next();
					Object key = entry.getKey();
					int keyHash = (key != null ? key.hashCode() : 0);
					Object newKey = resolveValue(key);
					int newKeyHash = (newKey != null ? newKey.hashCode() : 0);
					Object val = entry.getValue();
					Object newVal = resolveValue(val);
					newContent.put(newKey, newVal);
					entriesModified = entriesModified || (newVal != val || newKey != key || newKeyHash != keyHash);
				}
				
				return entriesModified ? newContent : value;
			}
			else if (value instanceof List) {
				List listVal = (List) value;
				List newContent = new ManagedList();
				boolean valueModified = false;
				
				for (int i = 0; i < listVal.size(); i++) {
					Object elem = listVal.get(i);
					Object newVal = resolveValue(elem);
					newContent.add(newVal);
					if (!ObjectUtils.nullSafeEquals(newVal, elem)) {
						valueModified = true;
					}
				}			
				return valueModified ? newContent : value;
			}
			else if (value instanceof Set) {
				Set setVal = (Set) value;
				Set newContent = new ManagedSet();
				boolean entriesModified = false;
				for (Iterator it = setVal.iterator(); it.hasNext();) {
					Object elem = it.next();
					int elemHash = (elem != null ? elem.hashCode() : 0);
					Object newVal = resolveValue(elem);
					int newValHash = (newVal != null ? newVal.hashCode() : 0);
					newContent.add(newVal);
					entriesModified = entriesModified || (newVal != elem || newValHash != elemHash);
				}	
				return entriesModified ? newContent : value;
			}
			else {
				return value;
			}			
		}

		@Override
		protected String resolvePlaceholder(String placeholder, Properties props) {						
			if ((executionScope != null)
					&& (executionScope.hasExecutionContext())) {
				Object obj = executionContext.findVariable(placeholder);
				if(obj != null) {
					return obj.toString();
				}
			}
			if (instantiationManager.isInFlowInitialization())
				return instantiationManager.getInitializingFlowParameter(
						placeholder).toString();
			else
				throw new SlcException("Could not resolve placeholder '" 
						+ placeholder + "' in bean '" + beanName + "'");
		}
	}
}
