package org.argeo.slc.core.execution;

import java.util.List;
import java.util.Map;

import org.argeo.api.cms.CmsLog;
import org.argeo.slc.SlcException;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

public abstract class AbstractExecutionFlowGenerator implements
		BeanFactoryPostProcessor, PriorityOrdered {
	private final CmsLog log = CmsLog.getLog(getClass());

	protected abstract Map<String, BeanDefinition> createExecutionFlowDefinitions(
			ConfigurableListableBeanFactory beanFactory);

	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactory) throws BeansException {
		if (!(beanFactory instanceof BeanDefinitionRegistry)) {
			throw new SlcException("Can only work on "
					+ BeanDefinitionRegistry.class);
		}

		Map<String, BeanDefinition> definitions = createExecutionFlowDefinitions(beanFactory);

		for (String beanName : definitions.keySet()) {
			if (log.isTraceEnabled())
				log.debug("Registering execution flow " + beanName);
			((BeanDefinitionRegistry) beanFactory).registerBeanDefinition(
					beanName, definitions.get(beanName));
		}
	}

	protected GenericBeanDefinition createDefaultFlowDefinition(
			List<Runnable> executables) {
		GenericBeanDefinition bd = new GenericBeanDefinition();
		bd.setBeanClass(DefaultExecutionFlow.class);

		MutablePropertyValues mpv = new MutablePropertyValues();
		mpv.addPropertyValue("executables", executables);

		bd.setPropertyValues(mpv);
		return bd;
	}

	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}

}
