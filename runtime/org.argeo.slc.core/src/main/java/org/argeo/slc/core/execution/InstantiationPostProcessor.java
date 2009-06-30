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

	private InstantiationManager instantiationManager;
	
	public InstantiationManager getInstantiationManager() {
		return instantiationManager;
	}

	public void setInstantiationManager(InstantiationManager instantiationManager) {
		this.instantiationManager = instantiationManager;
	}

	@Override
	public boolean postProcessAfterInstantiation(Object bean, String beanName)
			throws BeansException {
		if (bean instanceof ExecutionFlow)
			instantiationManager
					.flowInitializationStarted((ExecutionFlow) bean, beanName);
		return true;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		if (bean instanceof ExecutionFlow)
			instantiationManager
					.flowInitializationFinished((ExecutionFlow) bean, beanName);
		return bean;
	}

}
