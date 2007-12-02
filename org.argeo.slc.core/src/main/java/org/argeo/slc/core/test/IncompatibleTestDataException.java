package org.argeo.slc.core.test;

import org.argeo.slc.core.SlcException;

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
}
