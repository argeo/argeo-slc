/*
 * Copyright (C) 2007-2012 Mathieu Baudier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.argeo.slc.core.execution;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
	private final Log log = LogFactory.getLog(getClass());

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
