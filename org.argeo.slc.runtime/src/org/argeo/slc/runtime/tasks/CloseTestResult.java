package org.argeo.slc.runtime.tasks;

import org.argeo.slc.test.TestResult;

public class CloseTestResult implements Runnable {
	private TestResult testResult;

	public void run() {
		testResult.close();
	}

	public void setTestResult(TestResult testResult) {
		this.testResult = testResult;
	}

}
