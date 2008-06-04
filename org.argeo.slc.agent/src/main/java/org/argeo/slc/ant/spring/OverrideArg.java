package org.argeo.slc.ant.spring;

import java.util.List;
import java.util.Vector;

import org.apache.tools.ant.BuildException;

import org.argeo.slc.core.SlcException;

/** Ant type allowing to override bean properties. */
public class OverrideArg extends SpringArg<Object> {
	private String name;
	private Object value;
	private OverrideList overrideList;
	private MapArg overrideMap;
	private String antref;

	/** The name of the property to override. */
	public String getName() {
		return name;
	}

	/** Sets the name. */
	public void setName(String name) {
		this.name = name;
	}

	/** Sets a reference to an ant data type. */
	public void setAntref(String antref) {
		checkValueAlreadySet();
		this.antref = antref;
	}

	/** Both value and bean cannot be set. */
	public void setValue(String value) {
		checkValueAlreadySet();
		this.value = value;
	}

	@Override
	public void setBean(String bean) {
		checkValueAlreadySet();
		super.setBean(bean);
	}

	/** Creates override list sub tag. */
	public OverrideList createList() {
		checkValueAlreadySet();
		overrideList = new OverrideList();
		return overrideList;
	}

	public MapArg createMap() {
		checkValueAlreadySet();
		overrideMap = new MapArg();
		return overrideMap;
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
		} else if (overrideMap != null) {
			return overrideMap.getMap();
		} else if (antref != null) {
			Object obj = getProject().getReference(antref);
			if (obj == null) {
				throw new SlcException("No object found for reference "
						+ antref);
			}
			setOverridenProperties(obj);
			return obj;
		} else {
			throw new BuildException("Value or bean not set.");
		}
	}

	private void checkValueAlreadySet() {
		if (value != null || overrideList != null || antref != null
				|| getBean() != null || overrideMap != null) {
			throw new BuildException("Value already set.");
		}
	}

	/** List of overrides */
	protected class OverrideList {
		private List<OverrideArg> list = new Vector<OverrideArg>();

		/** Creates override sub tag. */
		public OverrideArg createOverride() {
			OverrideArg overrideArg = new OverrideArg();
			list.add(overrideArg);
			return overrideArg;
		}

		/** Gets as list of objects. */
		public List<Object> getAsObjectList() {
			List<Object> objectList = new Vector<Object>();
			for (OverrideArg arg : list) {
				objectList.add(arg.getObject());
			}
			return objectList;
		}
	}
}
