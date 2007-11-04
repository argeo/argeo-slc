package org.argeo.slc.core.test;

/** The result of a test */
public interface TestResult {
	/** Gets the id of the related test result. */
	public TestResultId getTestResultId();

	/** Adds a part of the result. */
	public void addResultPart(TestResultPart part);

	/**
	 * Marks that the collection of test results is completed and free the
	 * related resources (also closing listeners).
	 */
	public void close();
}
