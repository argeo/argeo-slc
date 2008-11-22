package org.argeo.slc.test;

public interface TestRunAware {
	/** Notifies the current test run. */
	public void notifyTestRun(TestRun testRun);

}
