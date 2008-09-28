package org.argeo.slc.autoui;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ConfigurableApplicationContext;

public class SpringStaticRefProvider implements StaticRefProvider {
	private final ConfigurableApplicationContext applicationContext;

	public SpringStaticRefProvider(
			ConfigurableApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public Object getStaticRef(String id) {
		try {
			return applicationContext.getBean(id);
		} catch (NoSuchBeanDefinitionException e) {
			// silent
			return null;
		}
	}

	public void close(){
		applicationContext.close();
	}
}
