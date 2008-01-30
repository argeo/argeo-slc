package org.argeo.slc.core.test.context;

import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.InitializingBean;

import org.argeo.slc.core.SlcException;

public class SimpleContextAware implements ContextAware, InitializingBean {
	private ParentContextAware parentContext;

	private Map<String, Object> values = new TreeMap<String, Object>();
	private Map<String, Object> expectedValues = new TreeMap<String, Object>();

	private String contextSkipFlag = "!";
	private String contextAnyFlag = "*";

	public Map<String, Object> getValues() {
		return values;
	}

	public void setValues(Map<String, Object> values) {
		this.values = values;
	}

	public Map<String, Object> getExpectedValues() {
		return expectedValues;
	}

	public void setExpectedValues(Map<String, Object> expectedValues) {
		this.expectedValues = expectedValues;
	}

	/** Used to add this context as a child by setting a property. */
	public void setParentContext(ParentContextAware parentContextAware) {
		if (parentContext != null)
			throw new SlcException("Parent context already set");
		this.parentContext = parentContextAware;
		this.parentContext.addChildContext(this);
	}

	protected ParentContextAware getParentContext() {
		return parentContext;
	}

	public void afterPropertiesSet() throws Exception {
		if (parentContext != null) {
			ContextUtils.synchronize(parentContext);
		}
	}

	public String getContextSkipFlag() {
		return contextSkipFlag;
	}

	public void setContextSkipFlag(String contextSkipFlag) {
		this.contextSkipFlag = contextSkipFlag;
	}

	public String getContextAnyFlag() {
		return contextAnyFlag;
	}

	public void setContextAnyFlag(String contextAnyFlag) {
		this.contextAnyFlag = contextAnyFlag;
	}

}
