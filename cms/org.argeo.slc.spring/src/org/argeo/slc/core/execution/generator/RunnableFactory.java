package org.argeo.slc.core.execution.generator;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;

/**
 * Interprets a <code>RunnableDataNode</code> by creating corresponding
 * beans and registering them in a <code>BeanDefinitionRegistry</code>
 *
 */
public interface RunnableFactory {

	public void createAndRegisterRunnable(RunnableDataNode node,
			BeanDefinitionRegistry beanDefinitionRegistry);
}
