package org.argeo.slc.core.test.context;

import java.beans.BeanInfo;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.InitializingBean;

public class SimpleContextAware implements ContextAware, InitializingBean {
	private SimpleParentContextAware parentContext;

	private Map<String, Object> values = new TreeMap<String, Object>();
	private Map<String, Object> expectedValues = new TreeMap<String, Object>();

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
	public void setParentContext(SimpleParentContextAware parentContextAware) {
		parentContextAware.addChildContext(this);
		this.parentContext = parentContextAware;
	}

	protected SimpleParentContextAware getParentContext() {
		return parentContext;
	}

	public void afterPropertiesSet() throws Exception {
		if (parentContext != null) {
			ContextUtils.synchronize(parentContext);
		}
	}

}
