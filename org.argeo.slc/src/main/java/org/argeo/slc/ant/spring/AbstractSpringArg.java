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

	// cache bean instance to avoid reading it twice if it is a prototype
	private Object beanInstance = null;

	/** The <u>name</u> of the underlying bean, as set throught the attribute. */
	public String getBean() {
		return bean;
	}

	/** Setter for the bean name. */
	public void setBean(String bean) {
		this.bean = bean;
	}

	/**
	 * Retrieve the instance of the bean. <b>The value is cached.</b>
	 */
	public Object getBeanInstance() {
		if (beanInstance == null) {
			beanInstance = getContext().getBean(bean);

			BeanWrapper wrapper = new BeanWrapperImpl(beanInstance);
			for (OverrideArg override : overrides) {
				wrapper.setPropertyValue(override.getName(), override
						.getObject());
			}
		}
		return beanInstance;
	}

	/** Creates an override subtag. */
	public OverrideArg createOverride() {
		OverrideArg propertyArg = new OverrideArg();
		overrides.add(propertyArg);
		return propertyArg;
	}

	/** The related Spring application context. */
	protected ApplicationContext getContext() {
		return (ApplicationContext) getProject().getReference(
				SlcProjectHelper.REF_ROOT_CONTEXT);
	}

}
