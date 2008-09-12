package org.argeo.slc.ant.spring;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;

/** Ant type allowing to override bean properties. */
public class OverrideArg extends SpringArg<Object> {
	private final static Log log = LogFactory.getLog(OverrideArg.class);

	private String name;
	private Object value;
	private ListArg overrideList;
	private MapArg overrideMap;

	private Boolean merge = false;

	/** The name of the property to override. */
	public String getName() {
		return name;
	}

	/** Sets the name. */
	public void setName(String name) {
		this.name = name;
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
	public ListArg createList() {
		checkValueAlreadySet();
		overrideList = new ListArg();
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
			if (log.isTraceEnabled())
				log.trace(this + "\t: Returns override object as value");
			return value;
		} else if (getBean() != null
				|| getAntref() != null
				// works on original if no collection is defined
				|| (getOriginal() != null && overrideList == null && overrideMap == null)) {
			if (log.isTraceEnabled())
				log.trace(this + "\t: Returns override object as instance");
			return getInstance();
		} else if (overrideList != null) {
			if (log.isTraceEnabled())
				log.trace(this + "\t: Returns override object as list");
			return overrideList.getAsObjectList((List<Object>) getOriginal());
		} else if (overrideMap != null) {
			if (log.isTraceEnabled())
				log.trace(this + "\t: Returns override object as map");
			return overrideMap
					.getAsObjectMap((Map<String, Object>) getOriginal());
		} else {
			throw new BuildException("Value or bean not set.");
		}
	}

	protected void checkValueAlreadySet() {
		super.checkValueAlreadySet();
		if (value != null || overrideList != null || overrideMap != null) {
			if (!getMerge()) {
				throw new BuildException("Value already set.");
			}
		}
	}

	public Boolean getMerge() {
		return merge;
	}

	public void setMerge(Boolean merge) {
		this.merge = merge;
	}

}
