package org.argeo.slc.core.test;

import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SimpleTestResult implements TestResult {
	private static Log log = LogFactory.getLog(SimpleTestResult.class);

	private TestResultId testResultId;
	private List<TestResultPart> parts = new Vector<TestResultPart>();

	public void addResultPart(TestResultPart part) {
		parts.add(part);
		if (log.isDebugEnabled())
			log.debug(part);
	}

	public void close() {
		parts.clear();
	}

	public TestResultId getTestResultId() {
		return testResultId;
	}

	public void setTestResultId(TestResultId testResultId) {
		this.testResultId = testResultId;
	}

	public List<TestResultPart> getParts() {
		return parts;
	}

}
