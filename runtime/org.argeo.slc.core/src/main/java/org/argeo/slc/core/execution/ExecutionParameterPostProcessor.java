package org.argeo.slc.core.execution;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.execution.ExecutionContext;
import org.argeo.slc.execution.ExecutionFlow;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.ManagedSet;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class ExecutionParameterPostProcessor extends
		InstantiationAwareBeanPostProcessorAdapter {

	private final static Log log = LogFactory
			.getLog(ExecutionParameterPostProcessor.class);

	private ExecutionContext executionContext;
	private InstantiationManager instantiationManager;

	private String placeholderPrefix = "@{";
	private String placeholderSuffix = "}";
	private String nullValue;

	@Override
	public PropertyValues postProcessPropertyValues(PropertyValues pvs,
			PropertyDescriptor[] pds, Object bean, String beanName)
			throws BeansException {

		// TODO: resolve at execution only if scope is execution
		// TODO: deal with placeholders in RuntimeBeanReference and
		// RuntimeBeanNameReference

		MutablePropertyValues newPvs = new MutablePropertyValues();

		boolean changesOccured = false;

		for (PropertyValue pv : pvs.getPropertyValues()) {
			Object convertedValue = resolveValue(beanName, bean, pv.getValue());
			newPvs.addPropertyValue(new PropertyValue(pv, convertedValue));
			if (convertedValue != pv.getValue()) {
				changesOccured = true;
			}
		}

		return changesOccured ? newPvs : pvs;
	}

	@Override
	public boolean postProcessAfterInstantiation(Object bean, String beanName)
			throws BeansException {
		if (bean instanceof ExecutionFlow)
			instantiationManager.flowInitializationStarted(
					(ExecutionFlow) bean, beanName);
		return true;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		if (bean instanceof ExecutionFlow)
			instantiationManager.flowInitializationFinished(
					(ExecutionFlow) bean, beanName);
		return bean;
	}

	protected String resolvePlaceholder(Object bean, String placeholder) {
		if (instantiationManager.isInFlowInitialization())
			return instantiationManager.getInitializingFlowParameter(
					placeholder).toString();

		else {// execution
			// next call fail if no execution context available
			Object obj = executionContext.getVariable(placeholder);
			if (obj != null) {
				return obj.toString();
			}
		}

		return null;
	}

	@SuppressWarnings(value = { "unchecked" })
	public Object resolveValue(String beanName, Object bean, Object value) {
		if (value instanceof TypedStringValue) {
			TypedStringValue tsv = (TypedStringValue) value;
			String originalValue = tsv.getValue();

			String convertedValue = resolveString(beanName, bean, originalValue);
			if (convertedValue == null)
				return null;
			return convertedValue.equals(originalValue) ? value
					: new TypedStringValue(convertedValue);
		} else if (value instanceof String) {
			String originalValue = value.toString();
			String convertedValue = resolveString(beanName, bean, originalValue);
			if (convertedValue == null)
				return null;
			return convertedValue.equals(originalValue) ? value
					: convertedValue;
		} else if (value instanceof ManagedMap) {
			Map mapVal = (Map) value;

			Map newContent = new ManagedMap();
			boolean entriesModified = false;
			for (Iterator it = mapVal.entrySet().iterator(); it.hasNext();) {
				Map.Entry entry = (Map.Entry) it.next();
				Object key = entry.getKey();
				int keyHash = (key != null ? key.hashCode() : 0);
				Object newKey = resolveValue(beanName, bean, key);
				int newKeyHash = (newKey != null ? newKey.hashCode() : 0);
				Object val = entry.getValue();
				Object newVal = resolveValue(beanName, bean, val);
				newContent.put(newKey, newVal);
				entriesModified = entriesModified
						|| (newVal != val || newKey != key || newKeyHash != keyHash);
			}

			return entriesModified ? newContent : value;
		} else if (value instanceof ManagedList) {
			List listVal = (List) value;
			List newContent = new ManagedList();
			boolean valueModified = false;

			for (int i = 0; i < listVal.size(); i++) {
				Object elem = listVal.get(i);
				Object newVal = resolveValue(beanName, bean, elem);
				newContent.add(newVal);
				if (!ObjectUtils.nullSafeEquals(newVal, elem)) {
					valueModified = true;
				}
			}
			return valueModified ? newContent : value;
		} else if (value instanceof ManagedSet) {
			Set setVal = (Set) value;
			Set newContent = new ManagedSet();
			boolean entriesModified = false;
			for (Iterator it = setVal.iterator(); it.hasNext();) {
				Object elem = it.next();
				int elemHash = (elem != null ? elem.hashCode() : 0);
				Object newVal = resolveValue(beanName, bean, elem);
				int newValHash = (newVal != null ? newVal.hashCode() : 0);
				newContent.add(newVal);
				entriesModified = entriesModified
						|| (newVal != elem || newValHash != elemHash);
			}
			return entriesModified ? newContent : value;
		} else {
			// log.debug(beanName + ": " + value.getClass() + " : " + value);
			return value;
		}

	}

	private String resolveString(String beanName, Object bean, String strVal) {
		// in case <null/> is passed
		if (strVal == null)
			return null;

		String value = parseStringValue(bean, strVal, new HashSet<String>());

		if (value == null)
			throw new SlcException("Could not resolve placeholder '" + strVal
					+ "' in bean '" + beanName + "'");

		return (value.equals(nullValue) ? null : value);
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

	public void setInstantiationManager(
			InstantiationManager instantiationManager) {
		this.instantiationManager = instantiationManager;
	}

	public void setExecutionContext(ExecutionContext executionContext) {
		this.executionContext = executionContext;
	}

	//
	// Following methods hacked from the internals of
	// PropertyPlaceholderConfigurer
	//

	protected String parseStringValue(Object bean, String strVal,
			Set<String> visitedPlaceholders)
			throws BeanDefinitionStoreException {

		// in case <null/> is passed
		if (strVal == null)
			return null;

		StringBuffer buf = new StringBuffer(strVal);

		int startIndex = strVal.indexOf(placeholderPrefix);
		while (startIndex != -1) {
			int endIndex = findPlaceholderEndIndex(buf, startIndex);
			if (endIndex != -1) {
				String placeholder = buf.substring(startIndex
						+ placeholderPrefix.length(), endIndex);
				if (!visitedPlaceholders.add(placeholder)) {
					throw new BeanDefinitionStoreException(
							"Circular placeholder reference '" + placeholder
									+ "' in property definitions");
				}
				// Recursive invocation, parsing placeholders contained in
				// the placeholder key.
				placeholder = parseStringValue(bean, placeholder,
						visitedPlaceholders);
				// Now obtain the value for the fully resolved key...
				String propVal = resolvePlaceholder(bean, placeholder);
				if (propVal != null) {
					// Recursive invocation, parsing placeholders contained
					// in the
					// previously resolved placeholder value.
					propVal = parseStringValue(bean, propVal,
							visitedPlaceholders);
					buf.replace(startIndex, endIndex
							+ placeholderSuffix.length(), propVal);
					if (log.isTraceEnabled()) {
						log.trace("Resolved placeholder '" + placeholder + "'");
					}
					startIndex = buf.indexOf(placeholderPrefix, startIndex
							+ propVal.length());
				} else {
					throw new BeanDefinitionStoreException(
							"Could not resolve placeholder '" + placeholder
									+ "'");
				}
				visitedPlaceholders.remove(placeholder);
			} else {
				startIndex = -1;
			}
		}

		return buf.toString();
	}

	private int findPlaceholderEndIndex(CharSequence buf, int startIndex) {
		int index = startIndex + placeholderPrefix.length();
		int withinNestedPlaceholder = 0;
		while (index < buf.length()) {
			if (StringUtils.substringMatch(buf, index, placeholderSuffix)) {
				if (withinNestedPlaceholder > 0) {
					withinNestedPlaceholder--;
					index = index + placeholderSuffix.length();
				} else {
					return index;
				}
			} else if (StringUtils
					.substringMatch(buf, index, placeholderPrefix)) {
				withinNestedPlaceholder++;
				index = index + placeholderPrefix.length();
			} else {
				index++;
			}
		}
		return -1;
	}

}
