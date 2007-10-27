package org.argeo.slc.testslc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.argeo.slc.core.test.IncompatibleTestDataException;
import org.argeo.slc.core.test.TestDefinition;
import org.argeo.slc.core.test.TestRun;

public class DummyTestDefinition implements TestDefinition {
	private Log log = LogFactory.getLog(DummyTestDefinition.class);

	public void execute(TestRun testRun) {
		if (!(testRun.getTestData() instanceof DummyTestData)) {
			throw new IncompatibleTestDataException(testRun.getTestData(), this);
		}
		DummyTestData testData = (DummyTestData) testRun.getTestData();

		if (testData.getReached().equals(testData.getExpected())) {
			log.info("Test passed");
		} else {
			log.info("Test failed");
		}
	}

}
