package org.argeo.api.slc.test;

import org.argeo.api.slc.SlcException;

/**
 * Exception to throw when a test definition cannot interpret the provided test
 * data.
 */
public class IncompatibleTestDataException extends SlcException {
	static final long serialVersionUID = 1l;

	public IncompatibleTestDataException(TestData testData,
			TestDefinition testDefinition) {
		super("TestData " + testData.getClass()
				+ " is not compatible with TestDefinition "
				+ testDefinition.getClass());
	}

	public IncompatibleTestDataException(TestRun testRun) {
		super("TestData " + ((TestData) testRun.getTestData()).getClass()
				+ " is not compatible with TestDefinition "
				+ ((TestDefinition) testRun.getTestDefinition()).getClass());
	}
}
