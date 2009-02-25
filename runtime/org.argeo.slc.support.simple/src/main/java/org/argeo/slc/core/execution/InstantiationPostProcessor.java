package org.argeo.slc.core.execution;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.execution.ExecutionFlow;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;

public class InstantiationPostProcessor extends
		InstantiationAwareBeanPostProcessorAdapter {
	private final static Log log = LogFactory
			.getLog(InstantiationPostProcessor.class);

	@Override
	public boolean postProcessAfterInstantiation(Object bean, String beanName)
			throws BeansException {
		if (bean instanceof ExecutionFlow)
			SimpleExecutionSpec.flowInitializationStarted((ExecutionFlow) bean);
		return true;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		if (bean instanceof ExecutionFlow)
			SimpleExecutionSpec
					.flowInitializationFinished((ExecutionFlow) bean);
		return bean;
	}
}
