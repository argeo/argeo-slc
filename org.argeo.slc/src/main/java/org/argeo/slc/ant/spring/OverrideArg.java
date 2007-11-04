package org.argeo.slc.ant.spring;

import java.util.List;
import java.util.Vector;

import org.apache.tools.ant.BuildException;

/** Ant type allowing to override bean properties. */
public class OverrideArg extends AbstractSpringArg {
	private String name;
	private Object value;
	private OverrideList overrideList;

	/** The name of the property to override. */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/** Both value and bean cannot be set. */
	public void setValue(String value) {
		if (getBean() != null || overrideList != null) {
			throw new BuildException("Value already set.");
		}
		this.value = value;
	}

	@Override
	public void setBean(String bean) {
		if (value != null || overrideList != null) {
			throw new BuildException("Value already set.");
		}
		super.setBean(bean);
	}

	public OverrideList createList() {
		if (value != null || getBean() != null) {
			throw new BuildException("Value already set.");
		}
		if (overrideList == null) {
			overrideList = new OverrideList();
		} else {
			throw new BuildException("Only one list can be declared");
		}
		return overrideList;
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
		} else if (overrideList != null) {
			return overrideList.getAsObjectList();
		} else {
			throw new BuildException("Value or bean not set.");
		}
	}

	/** List of overrides */
	protected class OverrideList {
		private List<OverrideArg> list = new Vector<OverrideArg>();

		public OverrideArg createOverride() {
			OverrideArg overrideArg = new OverrideArg();
			list.add(overrideArg);
			return overrideArg;
		}

		public List<Object> getAsObjectList() {
			List<Object> objectList = new Vector<Object>();
			for (OverrideArg arg : list) {
				objectList.add(arg.getObject());
			}
			return objectList;
		}
	}
}
