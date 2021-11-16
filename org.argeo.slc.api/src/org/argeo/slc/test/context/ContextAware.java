package org.argeo.slc.test.context;

import java.util.Map;

/** Access to an SLC test context that is, maps of reached and expected values. */
public interface ContextAware {
	public final static String DEFAULT_SKIP_FLAG = "!";
	public final static String DEFAULT_ANY_FLAG = "*";

	/** Retrieves reached values. */
	public Map<String, Object> getValues();

	/** Set reached values. */
	public void setValues(Map<String, Object> values);

	/** Retrieves expected values. */
	public Map<String, Object> getExpectedValues();

	public String getContextSkipFlag();

	public String getContextAnyFlag();
}
