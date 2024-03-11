package org.argeo.api.slc.execution;

import java.util.Map;

/**
 * The class implementing this interface defines the map of attributes that are
 * necessary for the corresponding ExecutionFlow.
 */
public interface ExecutionSpec {
	/**
	 * The name for an internal spec (for backward compatibility where a
	 * non-null name is expected)
	 */
	public final static String INTERNAL_NAME = "__SLC_EXECUTION_SPEC_INTERNAL";

	/**
	 * The name identifying the execution spec within its application context.
	 * Can be null. An execution spec can be referenced only if its name is not
	 * null or different from {@link #INTERNAL_NAME}
	 */
	public String getName();

	/** An optional description. Can be null. */
	public String getDescription();

	/** The attributes managed by this execution spec */
	public Map<String, ExecutionSpecAttribute> getAttributes();

}
