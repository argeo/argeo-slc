package org.argeo.api.slc.test;

/**
 * Part of a test result.
 * 
 * @see TestResult
 */
public interface TestResultPart {
	/** The status, as defined in {@link TestStatus}. */
	public Integer getStatus();

	/** The related message. */
	public String getMessage();

	/** The underlying <code>Exception</code>. Can be null. */
	public String getExceptionMessage();
}
