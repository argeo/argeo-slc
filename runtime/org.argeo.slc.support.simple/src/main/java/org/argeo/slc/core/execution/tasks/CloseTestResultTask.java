package org.argeo.slc.core.execution.tasks;

import org.argeo.slc.execution.Executable;
import org.argeo.slc.test.TestResult;

public class CloseTestResultTask implements Executable {
	private TestResult testResult;

	public void execute() {
		testResult.close();
	}

	public void setTestResult(TestResult testResult) {
		this.testResult = testResult;
	}

}
