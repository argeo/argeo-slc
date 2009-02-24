package org.argeo.slc.execution;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.generic.GenericBeanFactoryAccessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

public class ExecutionRegister implements InitializingBean,
		ApplicationContextAware {
	private final static Log log = LogFactory.getLog(ExecutionRegister.class);

	private ApplicationContext applicationContext;

	public ExecutionModuleDescriptor getDescriptor() {
		ExecutionModuleDescriptor md = new ExecutionModuleDescriptor();

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

	public void afterPropertiesSet() throws Exception {
		log.debug("Execution Module Descriptor:\n" + getDescriptor());
	}

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

}
