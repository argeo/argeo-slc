package org.argeo.slc.test;

import org.argeo.slc.SlcException;

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
		super("TestData " + testRun.getTestData().getClass()
				+ " is not compatible with TestDefinition "
				+ testRun.getTestDefinition().getClass());
	}
}
