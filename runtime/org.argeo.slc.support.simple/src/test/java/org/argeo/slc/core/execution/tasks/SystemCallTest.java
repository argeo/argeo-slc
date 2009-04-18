package org.argeo.slc.core.execution.tasks;

import org.argeo.slc.core.execution.AbstractExecutionFlowTestCase;

public class SystemCallTest extends AbstractExecutionFlowTestCase {
	private final String defFile = "systemCall.xml";

	public void testSystemCallSimple() throws Exception {
		configureAndExecuteSlcFlow(defFile, "systemCallSimple");
	}

	public void testSystemCallList() throws Exception {
		configureAndExecuteSlcFlow(defFile, "systemCallList");
	}

	public void testSystemCallOsSpecific() throws Exception {
		configureAndExecuteSlcFlow(defFile, "systemCallOsSpecific");
	}

	/*
	 * public void testSystemExecDir() throws Exception {
	 * configureAndExecuteSlcFlow(defFile, "systemCallExecDir"); }
	 */
	public void testSystemCallWithVar() throws Exception {
		configureAndExecuteSlcFlow(defFile, "systemCallWithVar");
	}

}
