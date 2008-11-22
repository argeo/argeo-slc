package org.argeo.slc.test.context;

import java.util.Map;

public interface ContextAware {
	public final static String DEFAULT_SKIP_FLAG = "!";
	public final static String DEFAULT_ANY_FLAG = "*";

	public Map<String, Object> getValues();

	public void setValues(Map<String, Object> values);

	public Map<String, Object> getExpectedValues();

	public String getContextSkipFlag();

	public String getContextAnyFlag();
}
