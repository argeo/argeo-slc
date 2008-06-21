package org.argeo.slc.core.test.tree;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.argeo.slc.core.test.TestResultListener;
import org.argeo.slc.core.test.TestResultPart;
import org.argeo.slc.core.test.TestStatus;

/**
 * Listener logging tree-based test results to the underlying logging system.
 * 
 * @see TreeTestResult
 * 
 */
public class TreeTestResultLogger implements TestResultListener<TreeTestResult> {

	private static Log log = LogFactory.getLog(TreeTestResultLogger.class);
	
	private Boolean logExceptionMessages = false;

	public void resultPartAdded(TreeTestResult testResult,
			TestResultPart testResultPart) {
		String msg = testResultPart + " - " + testResult.getUuid() + ":"
				+ testResult.getCurrentPath();
		if (testResultPart.getStatus().equals(TestStatus.PASSED)) {
			log.info(msg);
		} else if (testResultPart.getStatus().equals(TestStatus.FAILED)) {
			log.warn(msg);
		} else if (testResultPart.getStatus().equals(TestStatus.ERROR)) {
			if(logExceptionMessages || log.isDebugEnabled())
				msg = msg + "\n" + testResultPart.getExceptionMessage();
			
			log.error(msg);
		} else {
			log.error("Unknow test status: " + msg);
		}
	}

	public void close(TreeTestResult testResult) {
		log.info("Test result " + testResult.getUuid() + " closed.");
	}

	public void setLogExceptionMessages(Boolean logExceptionMessages) {
		this.logExceptionMessages = logExceptionMessages;
	}

	
}
