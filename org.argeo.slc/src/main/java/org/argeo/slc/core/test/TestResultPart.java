package org.argeo.slc.core.test;

/**
 * Part of a test result.
 * 
 * @see TestResult
 */
public interface TestResultPart {
	public TestStatus getStatus();

	public String getMessage();
}
