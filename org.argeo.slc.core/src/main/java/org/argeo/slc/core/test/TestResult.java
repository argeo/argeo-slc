package org.argeo.slc.core.test;

import java.util.Date;

/** The result of a test */
public interface TestResult extends TestStatus {
	/** Gets the id of the related test result. */
	public TestResultId getTestResultId();

	/** Adds a part of the result. */
	public void addResultPart(TestResultPart part);

	/**
	 * Marks that the collection of test results is completed and free the
	 * related resources (also closing listeners).
	 */
	public void close();

	/**
	 * The date when this test result was closed. Can be null, which means the
	 * result is not closed.
	 */
	public Date getCloseDate();
}
