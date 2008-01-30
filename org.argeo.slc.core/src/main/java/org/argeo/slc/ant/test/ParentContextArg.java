package org.argeo.slc.ant.test;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.tools.ant.types.DataType;

import org.argeo.slc.ant.spring.MapArg;
import org.argeo.slc.core.SlcException;
import org.argeo.slc.core.test.context.ContextAware;
import org.argeo.slc.core.test.context.ParentContextAware;

public class ParentContextArg extends DataType implements ParentContextAware {
	private MapArg values = null;
	private MapArg expectedValues = null;

	private List<ContextAware> children = new Vector<ContextAware>();

	public MapArg createValues() {
		values = new MapArg();
		return values;
	}

	public MapArg createExpectedValues() {
		expectedValues = new MapArg();
		return expectedValues;
	}

	public void addChildContext(ContextAware contextAware) {
		children.add(contextAware);
	}

	public Collection<ContextAware> getChildContexts() {
		return children;
	}

	public String getContextAnyFlag() {
		return DEFAULT_ANY_FLAG;
	}

	public String getContextSkipFlag() {
		return DEFAULT_SKIP_FLAG;
	}

	public Map<String, Object> getExpectedValues() {
		if (expectedValues == null)
			expectedValues = new MapArg();
		return expectedValues.getMap();
	}

	public Map<String, Object> getValues() {
		if (values == null)
			values = new MapArg();
		return values.getMap();
	}

	public void setValues(Map<String, Object> values) {
		throw new SlcException("Cannot override values map.");
	}

}
