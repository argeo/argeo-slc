package org.argeo.slc.core.test;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.argeo.slc.core.SlcException;

/**
 * Basic implementation of a test result containing only a list of result parts.
 */
public class SimpleTestResult implements TestResult {
	private static Log log = LogFactory.getLog(SimpleTestResult.class);

	private Boolean throwError = true;

	private TestResultId testResultId;
	private Date closeDate;
	private List<TestResultPart> parts = new Vector<TestResultPart>();

	public void addResultPart(TestResultPart part) {
		if (throwError && part.getStatus() == ERROR) {
			throw new SlcException("There was an error in the underlying test",
					part.getException());
		}
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

	public void setThrowError(Boolean throwError) {
		this.throwError = throwError;
	}

}
