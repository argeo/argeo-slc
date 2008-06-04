package org.argeo.slc.ant.spring;

import java.util.List;
import java.util.Vector;

import org.apache.tools.ant.types.DataType;
import org.argeo.slc.ant.SlcAntException;
import org.argeo.slc.ant.SlcProjectHelper;
import org.argeo.slc.core.SlcException;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;

/** Abstract Ant type wrapping a Spring bean. */
public class SpringArg<T> extends DataType {
	private List<OverrideArg> overrides = new Vector<OverrideArg>();

	private String bean;

	// cache bean instance to avoid reading it twice if it is a prototype
	private T beanInstance = null;

	/** The <u>name</u> of the underlying bean, as set through the attribute. */
	public String getBean() {
		return bean;
	}

	/** Setter for the bean name. */
	public void setBean(String bean) {
		this.bean = bean;
	}

	/**
	 * Retrieve the instance of the bean, and sets the overriden properties.
	 * <b>The value is cached.</b>
	 */
	public T getBeanInstance() {
		if (beanInstance == null) {
			beanInstance = (T)getContext().getBean(bean);
			
			setOverridenProperties(beanInstance);

			// FIXME: why are we doing this? Could not find any object using it
			if (beanInstance instanceof InitializingBean) {
				try {
					((InitializingBean) beanInstance).afterPropertiesSet();
				} catch (Exception e) {
					throw new SlcException("Could not initialize bean", e);
				}
			}
		}
		return beanInstance;
	}
	
	protected void setOverridenProperties(Object obj){
		BeanWrapper wrapper = new BeanWrapperImpl(obj);
		for (OverrideArg override : overrides) {
			if (override.getName() == null) {
				throw new SlcAntException(
						"The name of the property to override has to be set.");
			}

//			LogFactory.getLog(getClass()).debug(
//					"Prop " + override.getName());
			wrapper.setPropertyValue(override.getName(), override
					.getObject());
		}
	
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
