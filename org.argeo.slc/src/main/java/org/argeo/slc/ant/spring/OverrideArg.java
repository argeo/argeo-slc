package org.argeo.slc.ant.spring;

import org.apache.tools.ant.BuildException;

/** Ant type allowing to override bean properties. */
public class OverrideArg extends AbstractSpringArg {
	private String name;
	private Object value;

	/** The nbame of the property to override. */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/** Both value and bean cannot be set. */
	public void setValue(String value) {
		if (getBean() != null) {
			throw new BuildException(
					"Cannot set both 'bean' and 'value' attributes.");
		}
		this.value = value;
	}

	@Override
	public void setBean(String bean) {
		if (value != null) {
			throw new BuildException(
					"Cannot set both 'bean' and 'value' attributes.");
		}
		super.setBean(bean);
	}

	/**
	 * The related object: the value if a value had been set or an instance of
	 * the bean if not.
	 */
	public Object getObject() {
		if (value != null) {
			return value;
		} else if (getBean() != null) {
			return getBeanInstance();
		} else {
			throw new BuildException("Value or bean not set.");
		}
	}

}
