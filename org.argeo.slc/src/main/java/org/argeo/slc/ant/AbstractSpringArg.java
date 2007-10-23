package org.argeo.slc.ant;

import java.util.List;
import java.util.Vector;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.context.ApplicationContext;

import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.DataType;

public abstract class AbstractSpringArg extends DataType {
	private List<OverrideArg> overrides = new Vector<OverrideArg>();

	private String bean;

	public String getBean() {
		return bean;
	}

	public void setBean(String bean) {
		this.bean = bean;
	}

	protected Object getBeanInstance() {
		Object obj = getContext().getBean(bean);

		BeanWrapper wrapper = new BeanWrapperImpl(obj);
		for (OverrideArg override : overrides) {
			wrapper.setPropertyValue(override.getName(), override.getObject());
		}

		return obj;
	}

	public OverrideArg createOverride() {
		OverrideArg propertyArg = new OverrideArg();
		overrides.add(propertyArg);
		return propertyArg;
	}

	protected ApplicationContext getContext() {
		return (ApplicationContext) getProject().getReference(
				SlcProjectHelper.REF_ROOT_CONTEXT);
	}

}
