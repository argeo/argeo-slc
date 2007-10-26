package org.argeo.slc.testslc;

import org.argeo.slc.core.test.IncompatibleTestDataException;
import org.argeo.slc.core.test.TestDefinition;
import org.argeo.slc.core.test.TestRun;

public class DummyTestDefinition implements TestDefinition {

	public void execute(TestRun testRun) {
		if (!(testRun.getTestData() instanceof DummyTestData)) {
			throw new IncompatibleTestDataException(testRun.getTestData(), this);
		}
		DummyTestData testData = (DummyTestData) testRun.getTestData();

		if (testData.getReached().equals(testData.getExpected())) {
			stdOut("Test passed");
		} else {
			stdOut("Test failed");
		}
	}

	private static void stdOut(Object o) {
		System.out.println(o);
	}
}
