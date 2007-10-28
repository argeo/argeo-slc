package org.argeo.slc.core.test;

public interface TestResultListener {
	public void resultPartAdded(TestResult testResult,
			TestResultPart testResultPart);
	public void close();
}
