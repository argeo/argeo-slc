package org.argeo.api.slc.test;

/** Listener to the operations on a test result. */
public interface TestResultListener<T extends TestResult> {
	/** Notified when a part was added to a test result. */
	public void resultPartAdded(T testResult, TestResultPart testResultPart);

	/** Stops listening and release the related resources. */
	public void close(T testResult);
}
