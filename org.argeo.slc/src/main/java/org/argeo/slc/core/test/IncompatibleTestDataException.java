package org.argeo.slc.core.test;

/**
 * Excception to throw when a test definition cannot interpret the provided tets
 * data.
 */
public class IncompatibleTestDataException extends RuntimeException {
	static final long serialVersionUID = 1l;

	public IncompatibleTestDataException(TestData testData,
			TestDefinition testDefinition) {
		super("TestData " + testData.getClass()
				+ " is not compatible with TestDefinition "
				+ testDefinition.getClass());
	}
}
