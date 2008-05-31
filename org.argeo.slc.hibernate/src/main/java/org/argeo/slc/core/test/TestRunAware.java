package org.argeo.slc.core.test;

public interface TestRunAware {
	/** Notifies the current test run. */
	public void notifyTestRun(TestRun testRun);

}
