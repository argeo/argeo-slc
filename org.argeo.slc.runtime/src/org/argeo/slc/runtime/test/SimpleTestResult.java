package org.argeo.slc.runtime.test;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.Vector;

import org.argeo.api.cms.CmsLog;
import org.argeo.slc.SlcException;
import org.argeo.slc.test.TestResult;
import org.argeo.slc.test.TestResultPart;
import org.argeo.slc.test.TestRun;

/**
 * Basic implementation of a test result containing only a list of result parts.
 */
public class SimpleTestResult implements TestResult {
	private static CmsLog log = CmsLog.getLog(SimpleTestResult.class);

	private String uuid;
	private String currentTestRunUuid;

	private Boolean throwError = true;

	private Date closeDate;
	private List<TestResultPart> parts = new Vector<TestResultPart>();

	private Map<String, String> attributes = new TreeMap<String, String>();

	public void addResultPart(TestResultPart part) {
		if (throwError && part.getStatus() == ERROR) {
			throw new SlcException(
					"There was an error in the underlying test: "
							+ part.getExceptionMessage());
		}
		parts.add(part);
		if (log.isDebugEnabled())
			log.debug(part);
	}

	public void close() {
		parts.clear();
		closeDate = new Date();
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

	public void notifyTestRun(TestRun testRun) {
		currentTestRunUuid = testRun.getUuid();
	}

	public String getUuid() {
		if (uuid == null) {
			uuid = UUID.randomUUID().toString();
		}
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getCurrentTestRunUuid() {
		return currentTestRunUuid;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

}
