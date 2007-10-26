package org.argeo.slc.core.test;

/**
 * The programmatic definition of a test, which will be associated with
 * transient objects within a test run.
 */
public interface TestDefinition {
	/** Performs the test. */
	public void execute(TestRun testRun);
}
