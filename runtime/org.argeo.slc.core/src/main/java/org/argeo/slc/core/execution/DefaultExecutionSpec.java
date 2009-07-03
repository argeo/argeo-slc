package org.argeo.slc.core.execution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.execution.ExecutionSpec;
import org.argeo.slc.execution.ExecutionSpecAttribute;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

public class DefaultExecutionSpec implements ExecutionSpec, BeanNameAware,
		ApplicationContextAware, InitializingBean {
	private final static Log log = LogFactory
			.getLog(DefaultExecutionSpec.class);
	private ApplicationContext applicationContext;

	private String description;
	private Map<String, ExecutionSpecAttribute> attributes = new HashMap<String, ExecutionSpecAttribute>();

	private String name = getClass().getName() + "#" + UUID.randomUUID();

	public Map<String, ExecutionSpecAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, ExecutionSpecAttribute> attributes) {
		this.attributes = attributes;
	}

	public void setBeanName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public boolean equals(Object obj) {
		return ((ExecutionSpec) obj).getName().equals(name);
	}

	public String getDescription() {
		return description;
	}

	private ConfigurableListableBeanFactory getBeanFactory() {
		return ((ConfigurableApplicationContext) applicationContext)
				.getBeanFactory();
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public void afterPropertiesSet() throws Exception {
		if (description == null) {
			try {
				description = getBeanFactory().getBeanDefinition(name)
						.getDescription();
			} catch (NoSuchBeanDefinitionException e) {
				// silent
			}
		}

		for (String key : attributes.keySet()) {
			ExecutionSpecAttribute attr = attributes.get(key);
			if (attr instanceof RefSpecAttribute) {
				RefSpecAttribute rsa = (RefSpecAttribute) attr;
				if (rsa.getChoices() == null) {
					List<RefValueChoice> choices = buildRefValueChoices(rsa);
					if (log.isTraceEnabled())
						log.debug("Found " + choices.size() + " choices for "
								+ rsa + " in spec " + name);

					rsa.setChoices(choices);
				}
			}
		}
	}

	protected List<RefValueChoice> buildRefValueChoices(RefSpecAttribute rsa) {
		List<RefValueChoice> choices = new ArrayList<RefValueChoice>();
		if (applicationContext == null) {
			log.warn("No application context declared,"
					+ " cannot scan ref value choices.");
			return choices;
		}

		for (String beanName : getBeanFactory().getBeanNamesForType(
				rsa.getTargetClass(), true, false)) {
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
