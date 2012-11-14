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
package org.argeo.slc.core.execution.doc;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

public class ConsoleContextDescriber implements ContextDescriber {
	public void describeContext(BeanDefinitionRegistry registry) {
		String[] beanNames = registry.getBeanDefinitionNames();
		for (String beanName : beanNames) {
			log("\n## BEAN: " + beanName);
			describeBean(registry.getBeanDefinition(beanName));
		}
	}

	public void describeBean(BeanDefinition beanDefinition) {
		log("BeanDefinition class: "+beanDefinition.getClass());
		log("# ATTRIBUTES");
		for(String attr:beanDefinition.attributeNames()){
			log(attr+"="+beanDefinition.getAttribute(attr));
		}
		log("# PROPERTIES");
		MutablePropertyValues pValues = beanDefinition.getPropertyValues();
		for (PropertyValue pv : pValues.getPropertyValues()) {
			log(pv.getName() + "= (" + pv.getValue().getClass() + ") "
					+ pv.getValue());
		}
	}

	protected void log(Object obj){
		System.out.println(obj);
	}
}
