package org.argeo.slc.ant.test;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.tools.ant.types.DataType;

import org.argeo.slc.SlcException;
import org.argeo.slc.ant.spring.MapArg;
import org.argeo.slc.core.test.context.ContextUtils;
import org.argeo.slc.test.context.ContextAware;
import org.argeo.slc.test.context.ParentContextAware;

public class ParentContextType extends DataType implements ParentContextAware {
	private MapArg values = null;
	private MapArg expectedValues = null;

	private String contextAnyFlag = DEFAULT_ANY_FLAG;
	private String contextSkipFlag = DEFAULT_SKIP_FLAG;

	private String basedon = null;

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
		return contextAnyFlag;
	}

	public void setContextAnyFlag(String contextAnyFlag) {
		this.contextAnyFlag = contextAnyFlag;
	}

	public String getContextSkipFlag() {
		return contextSkipFlag;
	}

	public void setContextSkipFlag(String contextSkipFlag) {
		this.contextSkipFlag = contextSkipFlag;
	}

	public Map<String, Object> getExpectedValues() {
		if (expectedValues == null)
			expectedValues = new MapArg();
		if (basedon != null) {
			Map<String, Object> map = getBaseContext().getExpectedValues();
			ContextUtils.putNotContained(expectedValues.getMap(), map);
		}
		return expectedValues.getMap();
	}

	public Map<String, Object> getValues() {
		if (values == null)
			values = new MapArg();
		if (basedon != null) {
			Map<String, Object> map = getBaseContext().getValues();
			ContextUtils.putNotContained(values.getMap(), map);
		}
		return values.getMap();
	}

	private ParentContextType getBaseContext() {
		return (ParentContextType) getProject().getReference(basedon);
	}

	public void setValues(Map<String, Object> values) {
		throw new SlcException("Cannot override values map.");
	}

	public void setUpdateValues(Map<String, Object> overrideValues) {
		getValues().putAll(overrideValues);
	}

	public void setUpdateExpectedValues(
			Map<String, Object> overrideExpectedValues) {
		getExpectedValues().putAll(overrideExpectedValues);
	}

	public void setBasedon(String basedon) {
		this.basedon = basedon;
	}

}
