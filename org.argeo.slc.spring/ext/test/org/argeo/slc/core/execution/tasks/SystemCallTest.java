package org.argeo.slc.core.execution.tasks;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.core.execution.AbstractExecutionFlowTestCase;

public class SystemCallTest extends AbstractExecutionFlowTestCase {
	private final static Log log = LogFactory.getLog(SystemCallTest.class);

	private final String defFile = "systemCall.xml";

	public void testSystemCallSimple() throws Exception {
		if (isOsSupported())
			configureAndExecuteSlcFlow(defFile, "systemCallSimple");
	}

	public void testSystemCallList() throws Exception {
		if (isOsSupported())
			configureAndExecuteSlcFlow(defFile, "systemCallList");
	}

	public void testSystemCallOsSpecific() throws Exception {
		if (isOsSupported())
			configureAndExecuteSlcFlow(defFile, "systemCallOsSpecific");
	}

	public void testSystemCallWithVar() throws Exception {
		if (isOsSupported())
			configureAndExecuteSlcFlow(defFile, "systemCallWithVar");
	}

	protected boolean isOsSupported() {
		String osName = System.getProperty("os.name");
		final Boolean ret;
		if (osName.contains("Windows"))
			ret = false;
		else
			ret = true;

		if (ret == false)
			log.warn("Skip test because OS '" + osName + "' is not supported.");
		return ret;
	}
}
