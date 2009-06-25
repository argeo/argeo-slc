package org.argeo.slc.core.execution;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

public class ConsoleContextDescriber implements ContextDescriber {
	private final static Log log = LogFactory
			.getLog(ConsoleContextDescriber.class);

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
