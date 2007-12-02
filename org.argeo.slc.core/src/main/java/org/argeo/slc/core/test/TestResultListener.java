package org.argeo.slc.core.test;

/** Listener to the operations on a test result. */
public interface TestResultListener {
	/** Notified when a part was added to a test result. */
	public void resultPartAdded(TestResult testResult,
			TestResultPart testResultPart);

	/** Stops listening and release the related resources. */
	public void close(TestResult testResult);
}
