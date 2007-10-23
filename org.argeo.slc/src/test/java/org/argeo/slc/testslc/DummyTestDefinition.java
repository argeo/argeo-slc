package org.argeo.slc.testslc;

import org.argeo.slc.core.test.IncompatibleTestDataException;
import org.argeo.slc.core.test.TestData;
import org.argeo.slc.core.test.TestDefinition;

public class DummyTestDefinition implements TestDefinition {
	private DummyTestData testData;

	public void execute() {
		if (testData.getReached().equals(testData.getExpected())) {
			stdOut("Test passed");
		} else {
			stdOut("Test failed");
		}
	}

	public void setTestData(TestData testData) {
		if (!(testData instanceof DummyTestData)) {
			throw new IncompatibleTestDataException(testData, this);
		}
		this.testData = (DummyTestData) testData;
	}

	private static void stdOut(Object o) {
		System.out.println(o);
	}
}
