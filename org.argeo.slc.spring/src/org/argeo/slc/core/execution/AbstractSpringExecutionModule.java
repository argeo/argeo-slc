package org.argeo.slc.core.execution;

import org.argeo.slc.execution.ExecutionModule;

@Deprecated
public abstract class AbstractSpringExecutionModule implements ExecutionModule
		 {
/*
	protected ApplicationContext applicationContext;

	protected ExecutionContext executionContext;

	protected ExecutionFlowDescriptorConverter descriptorConverter = new DefaultDescriptorConverter();

	public ExecutionModuleDescriptor getDescriptor() {
		ExecutionModuleDescriptor md = new ExecutionModuleDescriptor();
		md.setName(getName());
		md.setVersion(getVersion());

		Map<String, ExecutionFlow> executionFlows = listFlows();
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

				if (executionFlow.isSetAsParameter(key)) {
					Object value = executionFlow.getParameter(key);
					if (attribute instanceof PrimitiveSpecAttribute) {
						PrimitiveValue primitiveValue = new PrimitiveValue();
						primitiveValue
								.setType(((PrimitiveSpecAttribute) attribute)
										.getType());
						primitiveValue.setValue(value);
						values.put(key, primitiveValue);
					} else if (attribute instanceof RefSpecAttribute) {
						RefValue refValue = new RefValue();
						if (value instanceof ScopedObject) {
							refValue.setLabel("RUNTIME "
									+ value.getClass().getName());
						} else {
							refValue.setLabel("STATIC "
									+ value.getClass().getName());
						}
						values.put(key, refValue);
					} else if (attribute instanceof ResourceSpecAttribute) {
						PrimitiveValue primitiveValue = new PrimitiveValue();
						primitiveValue
								.setType(((ResourceSpecAttribute) attribute)
										.getType());
						primitiveValue.setValue(value);
						values.put(key, primitiveValue);
					} else {
						throw new SlcException("Unkown spec attribute type "
								+ attribute.getClass());
					}
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

		return md;
	}

	protected Map<String, ExecutionFlow> listFlows() {
		GenericBeanFactoryAccessor accessor = new GenericBeanFactoryAccessor(
				applicationContext);
		Map<String, ExecutionFlow> executionFlows = accessor
				.getBeansOfType(ExecutionFlow.class);
		return executionFlows;
	}

	public void execute(ExecutionFlowDescriptor executionFlowDescriptor) {
		if (descriptorConverter != null)
			executionContext.addVariables(descriptorConverter
					.convertValues(executionFlowDescriptor));
		ExecutionFlow flow = (ExecutionFlow) applicationContext.getBean(
				executionFlowDescriptor.getName(), ExecutionFlow.class);
		flow.run();
	}

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

	public void setExecutionContext(ExecutionContext executionContext) {
		this.executionContext = executionContext;
	}

	public void setDescriptorConverter(
			ExecutionFlowDescriptorConverter descriptorConverter) {
		this.descriptorConverter = descriptorConverter;
	}*/

}
