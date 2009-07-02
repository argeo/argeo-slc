package org.argeo.slc.core.execution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
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

public class DefaultExecutionFlowDescriptorConverter implements
		ExecutionFlowDescriptorConverter, ApplicationContextAware {
	private final static Log log = LogFactory
			.getLog(DefaultExecutionFlowDescriptorConverter.class);

	private ApplicationContext applicationContext;

	public Map<String, Object> convertValues(
			ExecutionFlowDescriptor executionFlowDescriptor) {
		// convert the values of flow.getFlowDescriptor()
		Map<String, Object> values = executionFlowDescriptor.getValues();

		Map<String, Object> convertedValues = new HashMap<String, Object>();

		if (values != null) {
			for (String key : values.keySet()) {
				Object value = values.get(key);
				if (value instanceof PrimitiveValue) {
					PrimitiveValue primitiveValue = (PrimitiveValue) value;

					// TODO: check that the class of the the
					// primitiveValue.value
					// matches
					// the primitiveValue.type
					convertedValues.put(key, primitiveValue.getValue());
				} else if (value instanceof RefValue) {
					// not yet implemented

					// RefValue refValue = (RefValue) value;
					// convertedValues.put(key, refValue.getLabel());
				}
			}
		}
		return convertedValues;
	}

	public void addFlowsToDescriptor(ExecutionModuleDescriptor md,
			Map<String, ExecutionFlow> executionFlows) {
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
					values.put(key, buildRefValue((RefSpecAttribute) attribute,
							executionFlow, key));
				} else {
					throw new SlcException("Unkown spec attribute type "
							+ attribute.getClass());
				}

			}

			ExecutionFlowDescriptor efd = new ExecutionFlowDescriptor(name,
					values, executionSpec);
			if (executionFlow.getPath() != null)
				efd.setPath(executionFlow.getPath());

			// Add execution spec if necessary
			if (!md.getExecutionSpecs().contains(executionSpec))
				md.getExecutionSpecs().add(executionSpec);

			// Add execution flow
			md.getExecutionFlows().add(efd);
		}
	}

	@SuppressWarnings(value = { "unchecked" })
	protected RefValue buildRefValue(RefSpecAttribute rsa,
			ExecutionFlow executionFlow, String key) {
		RefValue refValue = new RefValue();

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
			if (ref == null)
				log.warn("Cannot define reference for ref spec attribute "
						+ key);
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

}
