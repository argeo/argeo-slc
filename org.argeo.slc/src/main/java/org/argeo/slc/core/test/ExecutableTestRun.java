package org.argeo.slc.core.test;

/** A test run that can be executed */
public interface ExecutableTestRun extends TestRun {

	/** Executes this test run. */
	public void execute();

}
