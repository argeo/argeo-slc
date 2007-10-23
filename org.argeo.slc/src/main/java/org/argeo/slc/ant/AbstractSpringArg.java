package org.argeo.slc.ant;

import java.util.List;
import java.util.Vector;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.context.ApplicationContext;

import org.apache.tools.ant.BuildException;

public abstract class AbstractSpringArg {
	private List<OverrideArg> overrides = new Vector<OverrideArg>();

	private String bean;
	private ApplicationContext context;

	public String getBean() {
		return bean;
	}

	public void setBean(String bean) {
		this.bean = bean;
	}

	protected Object getBeanInstance() {
		Object obj = context.getBean(bean);

		BeanWrapper wrapper = new BeanWrapperImpl(obj);
		for (OverrideArg override : overrides) {
			wrapper.setPropertyValue(override.getName(), override.getObject());
		}

		return obj;
	}

	public void setContext(ApplicationContext context) {
		this.context = context;
	}

	public OverrideArg createOverride() {
		OverrideArg propertyArg = new OverrideArg();
		propertyArg.setContext(context);
		overrides.add(propertyArg);
		return propertyArg;
	}
}
