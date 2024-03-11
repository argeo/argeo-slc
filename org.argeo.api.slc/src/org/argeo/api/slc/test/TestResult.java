package org.argeo.api.slc.test;

import java.util.Date;
import java.util.Map;

/** The result of a test */
public interface TestResult extends TestStatus, TestRunAware {
	public String getUuid();

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

	/** Additional arbitrary meta data */
	public Map<String, String> getAttributes();
}
