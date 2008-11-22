package org.argeo.slc.ant.spring;

import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.types.DataType;
import org.argeo.slc.ant.AntConstants;
import org.argeo.slc.core.SlcException;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;

/** Abstract Ant type wrapping a Spring bean. */
public class SpringArg<T> extends DataType {
	private final static Log log = LogFactory.getLog(SpringArg.class);

	private List<OverrideArg> overrides = new Vector<OverrideArg>();

	private String bean;
	private String antref;
	/**
	 * Reference to the original object, used to merge overrides. <b>this object
	 * will be modified</b>.
	 */
	private T original;

	// cache bean instance to avoid reading it twice if it is a prototype
	private T instance = null;

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
	public T getInstance() {
		if (instance == null) {
			if (log.isTraceEnabled())
				log.trace(this + "\t: Creates instance");

			if (bean != null) {
				instance = (T) getContext().getBean(bean);
				if (instance == null)
					throw new SlcException("No object found for Spring bean "
							+ bean);
			} else if (antref != null) {
				instance = (T) getProject().getReference(antref);
				if (instance == null)
					throw new SlcException("No object found for Ant reference "
							+ antref);
			} else if (original != null) {
				instance = original;
			} else {
				throw new SlcException(
						"Don't know how to retrieve bean instance");
			}

			setOverridenProperties(instance);

			// FIXME: why are we doing this? Could not find any object using it
			if (instance instanceof InitializingBean) {
				try {
					((InitializingBean) instance).afterPropertiesSet();
				} catch (Exception e) {
					throw new SlcException("Could not initialize bean", e);
				}
			}
		} else {
			if (log.isTraceEnabled())
				log.trace(this + "\t: Returns cached instance");
		}
		return instance;
	}

	protected void setOverridenProperties(Object obj) {
		BeanWrapper wrapper = new BeanWrapperImpl(obj);
		for (OverrideArg override : overrides) {
			if (override.getName() == null) {
				throw new SlcException(
						"The name of the property to override has to be set.");
			}

			if (log.isTraceEnabled())
				log.trace(this + "\t: Overrides property " + override.getName()
						+ " with " + override);

			if (override.getMerge() == true) {
				// if override is marked as merged retrieve the value and set is
				// as original
				override.setOriginal(wrapper.getPropertyValue(override
						.getName()));
			}
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
				AntConstants.REF_ROOT_CONTEXT);
	}

	protected void checkValueAlreadySet() {
		if (antref != null || bean != null || original != null) {
			throw new SlcException("Instance value already defined.");
		}
	}

	public void setOriginal(T original) {
		checkValueAlreadySet();
		this.original = original;
	}

	public T getOriginal() {
		return original;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer(getClass().getSimpleName());
		if (bean != null) {
			buf.append("#bean=").append(bean);
		} else if (antref != null) {
			buf.append("#antref=").append(antref);
		} else if (original != null) {
			buf.append("#orig=").append(original.hashCode());
		} else {
			buf.append("#noid");
		}
		buf.append("#").append(hashCode());
		return buf.toString();
	}
}
