package org.argeo.slc.core.execution;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.UnsupportedException;
import org.argeo.slc.execution.ExecutionFlow;
import org.argeo.slc.execution.ExecutionFlowDescriptor;
import org.argeo.slc.execution.ExecutionFlowDescriptorConverter;
import org.argeo.slc.execution.ExecutionModuleDescriptor;
import org.argeo.slc.execution.ExecutionSpec;
import org.argeo.slc.execution.ExecutionSpecAttribute;
import org.springframework.aop.scope.ScopedObject;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class DefaultExecutionFlowDescriptorConverter implements
		ExecutionFlowDescriptorConverter, ApplicationContextAware {
	public final static String REF_VALUE_TYPE_BEAN_NAME = "beanName";

	/** Workaround for https://www.spartadn.com/bugzilla/show_bug.cgi?id=206 */
	private final static String REF_VALUE_INTERNAL = "[internal]";

	private final static Log log = LogFactory
			.getLog(DefaultExecutionFlowDescriptorConverter.class);

	private ApplicationContext applicationContext;

	public Map<String, Object> convertValues(
			ExecutionFlowDescriptor executionFlowDescriptor) {
		Map<String, Object> values = executionFlowDescriptor.getValues();
		Map<String, Object> convertedValues = new HashMap<String, Object>();
		ExecutionSpec executionSpec = executionFlowDescriptor
				.getExecutionSpec();

		if (executionSpec == null)
			log.warn("Execution spec is null for " + executionFlowDescriptor);

		if (values != null && executionSpec != null) {
			values: for (String key : values.keySet()) {
				ExecutionSpecAttribute attribute = executionSpec
						.getAttributes().get(key);

				if (attribute.getIsFrozen())
					continue values;

				Object value = values.get(key);
				if (value instanceof PrimitiveValue) {
					PrimitiveValue primitiveValue = (PrimitiveValue) value;
					// TODO: check class <=> type
					convertedValues.put(key, primitiveValue.getValue());
				} else if (value instanceof RefValue) {
					RefValue refValue = (RefValue) value;

					if (REF_VALUE_TYPE_BEAN_NAME.equals(refValue.getType())) {
						String ref = refValue.getRef();
						if (ref != null && !ref.equals(REF_VALUE_INTERNAL)) {
							Object obj = applicationContext.getBean(refValue
									.getRef());
							convertedValues.put(key, obj);
						} else {
							log.warn("Cannot interpret " + refValue);
						}
					} else {
						throw new UnsupportedException("Ref value type",
								refValue.getType());
					}
				}
			}
		}
		return convertedValues;
	}

	public void addFlowsToDescriptor(ExecutionModuleDescriptor md,
			Map<String, ExecutionFlow> executionFlows) {
		SortedSet<ExecutionFlowDescriptor> set = new TreeSet<ExecutionFlowDescriptor>(
				new ExecutionFlowDescriptorComparator());
		for (String name : executionFlows.keySet()) {
			ExecutionFlow executionFlow = executionFlows.get(name);

			Assert.notNull(executionFlow.getName());
			Assert.state(name.equals(executionFlow.getName()));

			ExecutionSpec executionSpec = executionFlow.getExecutionSpec();
			Assert.notNull(executionSpec);
			Assert.notNull(executionSpec.getName());

			Map<String, Object> values = new TreeMap<String, Object>();
			for (String key : executionSpec.getAttributes().keySet()) {
				ExecutionSpecAttribute attribute = executionSpec
						.getAttributes().get(key);

				if (attribute instanceof PrimitiveSpecAttribute) {
					if (executionFlow.isSetAsParameter(key)) {
						Object value = executionFlow.getParameter(key);
						PrimitiveValue primitiveValue = new PrimitiveValue();
						primitiveValue
								.setType(((PrimitiveSpecAttribute) attribute)
										.getType());
						primitiveValue.setValue(value);
						values.put(key, primitiveValue);
					} else {
						// no need to add a primitive value if it is not set,
						// all necessary information is in the spec
					}
				} else if (attribute instanceof RefSpecAttribute) {
					if (attribute.getIsFrozen()) {
						values.put(key, new RefValue(REF_VALUE_INTERNAL));
					} else
						values.put(key, buildRefValue(
								(RefSpecAttribute) attribute, executionFlow,
								key));
				} else {
					throw new SlcException("Unkown spec attribute type "
							+ attribute.getClass());
				}

			}

			ExecutionFlowDescriptor efd = new ExecutionFlowDescriptor(name,
					values, executionSpec);
			if (executionFlow.getPath() != null)
				efd.setPath(executionFlow.getPath());
			else
				efd.setPath("");

			// Takes description from spring
			BeanDefinition bd = getBeanFactory().getBeanDefinition(name);
			efd.setDescription(bd.getDescription());

			// Add execution spec if necessary
			if (!md.getExecutionSpecs().contains(executionSpec))
				md.getExecutionSpecs().add(executionSpec);

			// Add execution flow
			set.add(efd);
			// md.getExecutionFlows().add(efd);
		}
		md.getExecutionFlows().addAll(set);
	}

	@SuppressWarnings(value = { "unchecked" })
	protected RefValue buildRefValue(RefSpecAttribute rsa,
			ExecutionFlow executionFlow, String key) {
		RefValue refValue = new RefValue();
		refValue.setType(REF_VALUE_TYPE_BEAN_NAME);

		if (executionFlow.isSetAsParameter(key)) {
			String ref = null;
			Object value = executionFlow.getParameter(key);
			if (applicationContext == null) {
				log
						.warn("No application context declared, cannot scan ref value.");
				ref = value.toString();
			} else {

				// look for a ref to the value
				Map<String, Object> beans = getBeanFactory().getBeansOfType(
						rsa.getTargetClass(), false, false);
				// TODO: also check scoped beans
				beans: for (String beanName : beans.keySet()) {
					Object obj = beans.get(beanName);
					if (value instanceof ScopedObject) {
						// don't call methods of the target of the scope
						if (obj instanceof ScopedObject)
							if (value == obj) {
								ref = beanName;
								break beans;
							}
					} else {
						if (obj.equals(value)) {
							ref = beanName;
							break beans;
						}
					}
				}
			}
			if (ref == null) {
				log.warn("Cannot define reference for ref spec attribute "
						+ key + " in " + executionFlow + " (" + rsa + ")."
						+ " If it is an inner bean consider put it frozen.");
				ref = REF_VALUE_INTERNAL;
			} else {
				if (log.isDebugEnabled())
					log.debug(ref + " is the reference for ref spec attribute "
							+ key + " in " + executionFlow + " (" + rsa + ")");
			}
			refValue.setRef(ref);
		}
		return refValue;
	}

	private ConfigurableListableBeanFactory getBeanFactory() {
		return ((ConfigurableApplicationContext) applicationContext)
				.getBeanFactory();
	}

	/** Must be use within the execution application context */
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

	private static class ExecutionFlowDescriptorComparator implements
			Comparator<ExecutionFlowDescriptor> {
		public int compare(ExecutionFlowDescriptor o1,
				ExecutionFlowDescriptor o2) {
			if (StringUtils.hasText(o1.getPath())
					&& StringUtils.hasText(o2.getPath())) {
				return o1.getPath().compareTo(o2.getPath());
			} else if (!StringUtils.hasText(o1.getPath())
					&& StringUtils.hasText(o2.getPath())) {
				return 1;
			} else if (StringUtils.hasText(o1.getPath())
					&& !StringUtils.hasText(o2.getPath())) {
				return -1;
			} else if (!StringUtils.hasText(o1.getPath())
					&& !StringUtils.hasText(o2.getPath())) {
				return o1.getName().compareTo(o2.getName());
			} else {
				return 0;
			}
		}

	}
}
