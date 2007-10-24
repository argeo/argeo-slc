package org.argeo.slc.core.test;

/**
 * The programmatic definition of a test, which will be associated with test
 * data within a test run.
 */
public interface TestDefinition {
	/** Perform the test. */
	public void execute();

	/** Initialize the test data */
	public void setTestData(TestData testData);
}
