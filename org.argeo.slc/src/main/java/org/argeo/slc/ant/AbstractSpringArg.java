package org.argeo.slc.ant;

import org.springframework.context.ApplicationContext;

public abstract class AbstractSpringArg {

	private String bean;
	private ApplicationContext context;

	public String getBean() {
		return bean;
	}

	public void setBean(String bean) {
		this.bean = bean;
	}
	
	protected Object getBeanInstance(){
		return context.getBean(bean);
	}

	public void setContext(ApplicationContext context) {
		this.context = context;
	}
	
	
}
