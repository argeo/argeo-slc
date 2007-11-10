package org.argeo.slc.core.test;

/** Simple statuses. */
public enum TestStatus {
	/** Test passed */
	PASSED,
	/** Test failed: the behavior was not the expected one */
	FAILED,
	/**
	 * Test could not run properly because of an unexpected issue: their can be
	 * no feedback on the behavior of the tested component
	 */
	ERROR
}
