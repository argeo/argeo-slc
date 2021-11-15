package org.argeo.slc.test;

/** Allows a test run to notify other objects. */
public interface TestRunAware {
	/** Notifies the current test run. */
	public void notifyTestRun(TestRun testRun);

}
