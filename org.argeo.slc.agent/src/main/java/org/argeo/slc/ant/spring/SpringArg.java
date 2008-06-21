package org.argeo.slc.ant.spring;

import java.util.List;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.DataType;
import org.argeo.slc.ant.SlcAntConstants;
import org.argeo.slc.core.SlcException;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;

/** Abstract Ant type wrapping a Spring bean. */
public class SpringArg<T> extends DataType {
	private List<OverrideArg> overrides = new Vector<OverrideArg>();

	private String bean;
	private String antref;

	// cache bean instance to avoid reading it twice if it is a prototype
	private T beanInstance = null;

	/** The <u>name</u> of the underlying bean, as set through the attribute. */
	public String getBean() {
		return bean;
	}

	/** Setter for the bean name. */
	public void setBean(String bean) {
		checkValueAlreadySet();
		this.bean = bean;
	}

	public String getAntref() {
		return antref;
	}

	/** Sets a reference to an ant data type. */
	public void setAntref(String antref) {
		checkValueAlreadySet();
		this.antref = antref;
	}

	/**
	 * Retrieve the instance of the bean, and sets the overridden properties.
	 * <b>The value is cached.</b>
	 */
	public T getBeanInstance() {
		if (beanInstance == null) {
			if (bean != null) {
				beanInstance = (T) getContext().getBean(bean);
				if (beanInstance == null)
					throw new SlcException("No object found for Spring bean "
							+ bean);
			} else if (antref != null) {
				beanInstance = (T) getProject().getReference(antref);
				if (beanInstance == null)
					throw new SlcException("No object found for Ant reference "
							+ antref);
			} else {
				throw new SlcException(
						"Don't know how to retrieve bean instance");
			}

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

	protected void setOverridenProperties(Object obj) {
		BeanWrapper wrapper = new BeanWrapperImpl(obj);
		for (OverrideArg override : overrides) {
			if (override.getName() == null) {
				throw new SlcException(
						"The name of the property to override has to be set.");
			}

			// LogFactory.getLog(getClass()).debug(
			// "Prop " + override.getName());
			wrapper.setPropertyValue(override.getName(), override.getObject());
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
				SlcAntConstants.REF_ROOT_CONTEXT);
	}

	protected void checkValueAlreadySet() {
		if (antref != null || bean != null) {
			throw new BuildException("Value already set.");
		}
	}

}
