package org.argeo.api.slc.test;

/**
 * The programmatic definition of a test, which will be associated with
 * transient objects within a test run.
 */
public interface TestDefinition extends TestStatus {
	/** Performs the test. */
	public void execute(TestRun testRun);
}
