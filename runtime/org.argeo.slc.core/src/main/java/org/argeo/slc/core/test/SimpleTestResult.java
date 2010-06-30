/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.argeo.slc.core.test;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.argeo.slc.SlcException;
import org.argeo.slc.test.TestResult;
import org.argeo.slc.test.TestResultPart;
import org.argeo.slc.test.TestRun;

/**
 * Basic implementation of a test result containing only a list of result parts.
 */
public class SimpleTestResult implements TestResult {
	private static Log log = LogFactory.getLog(SimpleTestResult.class);

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

}
