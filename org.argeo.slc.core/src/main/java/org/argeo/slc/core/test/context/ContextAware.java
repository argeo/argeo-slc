package org.argeo.slc.core.test.context;

import java.util.Map;

public interface ContextAware {
	public Map<String, Object> getValues();

	public void setValues(Map<String, Object> values);

	public Map<String, Object> getExpectedValues();

}
