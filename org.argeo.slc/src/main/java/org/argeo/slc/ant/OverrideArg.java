package org.argeo.slc.ant;

import org.apache.tools.ant.BuildException;

public class OverrideArg extends AbstractSpringArg {
	private String name;
	private Object value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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
