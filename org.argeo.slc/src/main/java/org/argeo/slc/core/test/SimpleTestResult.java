package org.argeo.slc.core.test;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Basic implementation of a test result containing only a list of result
 * parts.
 */
public class SimpleTestResult implements TestResult {
	private static Log log = LogFactory.getLog(SimpleTestResult.class);

	private TestResultId testResultId;
	private Date closeDate;
	private List<TestResultPart> parts = new Vector<TestResultPart>();

	public void addResultPart(TestResultPart part) {
		parts.add(part);
		if (log.isDebugEnabled())
			log.debug(part);
	}

	public void close() {
		parts.clear();
		closeDate = new Date();
	}

	public TestResultId getTestResultId() {
		return testResultId;
	}

	/** Sets the test result id. */
	public void setTestResultId(TestResultId testResultId) {
		this.testResultId = testResultId;
	}

	public List<TestResultPart> getParts() {
		return parts;
	}

	public Date getCloseDate() {
		return closeDate;
	}

}
