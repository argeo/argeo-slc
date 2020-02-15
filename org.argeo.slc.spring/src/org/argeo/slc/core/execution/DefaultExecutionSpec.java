/*
 * Copyright (C) 2007-2012 Argeo GmbH
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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.execution.ExecutionSpecAttribute;
import org.argeo.slc.execution.RefSpecAttribute;
import org.argeo.slc.execution.RefValueChoice;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

/** Spring based implementation of execution specifications. */
@Deprecated
public class DefaultExecutionSpec extends org.argeo.slc.runtime.DefaultExecutionSpec
		implements BeanNameAware, ApplicationContextAware, InitializingBean {
	private static final long serialVersionUID = 5159882223926926539L;
	private final static Log log = LogFactory.getLog(DefaultExecutionSpec.class);
	private transient ApplicationContext applicationContext;

	public DefaultExecutionSpec() {
		super();
	}

	public void setBeanName(String name) {
		setName(name);
	}

	private ConfigurableListableBeanFactory getBeanFactory() {
		return ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public void afterPropertiesSet() throws Exception {
		if (getDescription() == null) {
			try {
				setDescription(getBeanFactory().getBeanDefinition(getName()).getDescription());
			} catch (NoSuchBeanDefinitionException e) {
				// silent
			}
		}

		for (String key : getAttributes().keySet()) {
			ExecutionSpecAttribute attr = getAttributes().get(key);
			if (attr instanceof RefSpecAttribute) {
				RefSpecAttribute rsa = (RefSpecAttribute) attr;
				if (rsa.getChoices() == null) {
					List<RefValueChoice> choices = buildRefValueChoices(rsa);
					rsa.setChoices(choices);
				}
				if (log.isTraceEnabled())
					log.debug("Spec attr " + key + " has " + rsa.getChoices().size() + " choices");
			}
		}
	}

	/**
	 * Generates a list of ref value choices based on the bean available in the
	 * application context.
	 */
	protected List<RefValueChoice> buildRefValueChoices(RefSpecAttribute rsa) {
		List<RefValueChoice> choices = new ArrayList<RefValueChoice>();
		if (applicationContext == null) {
			log.warn("No application context declared," + " cannot scan ref value choices.");
			return choices;
		}

		beanNames: for (String beanName : getBeanFactory().getBeanNamesForType(rsa.getTargetClass(), true, false)) {

			// Since Spring 3, systemProperties is implicitly defined but has no
			// bean definition
			if (beanName.equals("systemProperties"))
				continue beanNames;

			BeanDefinition bd = getBeanFactory().getBeanDefinition(beanName);
			RefValueChoice choice = new RefValueChoice();
			choice.setName(beanName);
			choice.setDescription(bd.getDescription());
			if (log.isTraceEnabled())
				log.debug("Found choice " + beanName + " for " + rsa);

			choices.add(choice);

		}
		return choices;
	}

}
