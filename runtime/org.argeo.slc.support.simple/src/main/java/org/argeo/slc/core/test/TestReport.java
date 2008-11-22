package org.argeo.slc.core.test;

/**
 * A report that can be generated based on a given test result. <b>This
 * interface may change in the future.</b>
 */
public interface TestReport {
	/** Performs the actions necessary to generate a report. */
	public void generateTestReport(TestResult result);
}
