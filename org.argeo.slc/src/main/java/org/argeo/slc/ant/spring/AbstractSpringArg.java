package org.argeo.slc.ant.spring;

import java.util.List;
import java.util.Vector;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.context.ApplicationContext;

import org.apache.tools.ant.types.DataType;

import org.argeo.slc.ant.SlcProjectHelper;

/** Abstract Ant type wrapping a Spring bean. */
public abstract class AbstractSpringArg extends DataType {
	private List<OverrideArg> overrides = new Vector<OverrideArg>();

	private String bean;

	/** The <u>name</u> of the underlying bean, as set throught the attribute. */
	public String getBean() {
		return bean;
	}

	/** Setter for the bean name. */
	public void setBean(String bean) {
		this.bean = bean;
	}

	/**
	 * Retrieve the instance of the bean. <b>If teh underlying Spring bean is a
	 * prototype, it will instanciated each time.</b>
	 */
	protected Object getBeanInstance() {
		Object obj = getContext().getBean(bean);

		BeanWrapper wrapper = new BeanWrapperImpl(obj);
		for (OverrideArg override : overrides) {
			wrapper.setPropertyValue(override.getName(), override.getObject());
		}

		return obj;
	}

	/** Creates an override subtag.*/
	public OverrideArg createOverride() {
		OverrideArg propertyArg = new OverrideArg();
		overrides.add(propertyArg);
		return propertyArg;
	}

	/** The related Spring application context.*/
	protected ApplicationContext getContext() {
		return (ApplicationContext) getProject().getReference(
				SlcProjectHelper.REF_ROOT_CONTEXT);
	}

}
