package org.argeo.slc.core.execution;

import java.util.HashMap;
import java.util.Map;

import org.argeo.slc.execution.ExecutionFlow;
import org.argeo.slc.execution.ExecutionFlowDescriptor;
import org.argeo.slc.execution.ExecutionModule;
import org.argeo.slc.execution.ExecutionModuleDescriptor;
import org.argeo.slc.execution.ExecutionSpec;
import org.argeo.slc.execution.ExecutionSpecAttribute;
import org.argeo.slc.process.SlcExecution;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.generic.GenericBeanFactoryAccessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

public abstract class AbstractSpringExecutionModule implements ExecutionModule,
		ApplicationContextAware {
	private ApplicationContext applicationContext;

	public ExecutionModuleDescriptor getDescriptor() {
		ExecutionModuleDescriptor md = new ExecutionModuleDescriptor();
		md.setName(getName());
		md.setVersion(getVersion());

		GenericBeanFactoryAccessor accessor = new GenericBeanFactoryAccessor(
				applicationContext);
		Map<String, ExecutionFlow> executionFlows = accessor
				.getBeansOfType(ExecutionFlow.class);

		for (String name : executionFlows.keySet()) {
			ExecutionFlow executionFlow = executionFlows.get(name);

			Assert.notNull(executionFlow.getName());
			Assert.state(name.equals(executionFlow.getName()));

			ExecutionSpec executionSpec = executionFlow.getExecutionSpec();
			Assert.notNull(executionSpec);
			Assert.notNull(executionSpec.getName());

			Map<String, Object> values = new HashMap<String, Object>();
			for (String key : executionSpec.getAttributes().keySet()) {
				ExecutionSpecAttribute attribute = executionSpec
						.getAttributes().get(key);
				if (attribute instanceof SimpleExecutionSpec
						&& attribute.getIsParameter()) {
					values.put(key, executionFlow.getParameter(key));
				}
			}

			ExecutionFlowDescriptor efd = new ExecutionFlowDescriptor(name,
					values, executionSpec);

			if (!md.getExecutionSpecs().contains(executionSpec))
				md.getExecutionSpecs().add(executionSpec);
			md.getExecutionFlows().add(efd);
		}

		return md;
	}

	public void execute(SlcExecution slcExecution) {
		applicationContext.publishEvent(new NewExecutionEvent(this,
				slcExecution));
	}

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

}
