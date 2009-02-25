package org.argeo.slc.core.execution;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

public interface ContextDescriber {
	public void describeContext(BeanDefinitionRegistry registry);
	public void describeBean(BeanDefinition bd);
}
