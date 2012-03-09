/*
 * Copyright (C) 2007-2012 Mathieu Baudier
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

import java.io.Serializable;

import org.argeo.slc.test.TestResultPart;
import org.argeo.slc.test.TestRun;
import org.argeo.slc.test.TestRunAware;
import org.argeo.slc.test.TestStatus;

/**
 * <p>
 * Basic implementation of a result part, implementing the standard three status
 * approach for test results.
 * </p>
 * 
 * @see TestStatus
 */
public class SimpleResultPart implements TestResultPart, TestStatus,
		TestRunAware, Serializable {
	private static final long serialVersionUID = 6669675957685071901L;

	private Long tid;

	private String testRunUuid;

	/** The status. Default to ERROR since it should always be explicitely set. */
	private Integer status = ERROR;
	private String message;
	private String exceptionMessage;

	public SimpleResultPart() {
	}

	public SimpleResultPart(Integer status, String message) {
		this(status, message, null);
	}

	public SimpleResultPart(Integer status, String message, Exception exception) {
		this.status = status;
		this.message = message;
		setException(exception);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getStatus() {
		return status;
	}

	public String getExceptionMessage() {
		return exceptionMessage;
	}

	public void setException(Exception exception) {
		if (exception == null)
			return;

		StringBuffer buf = new StringBuffer("");
		buf.append(exception.toString());
		buf.append('\n');
		for (StackTraceElement elem : exception.getStackTrace()) {
			buf.append('\t').append(elem.toString()).append('\n');
		}

		if (exception.getCause() != null)
			addRootCause(buf, exception.getCause());

		this.exceptionMessage = buf.toString();
	}

	protected void addRootCause(StringBuffer buf, Throwable cause) {
		if (cause == null)
			return;

		buf.append("Caused by: " + cause.getMessage());
		for (StackTraceElement elem : cause.getStackTrace()) {
			buf.append('\t').append(elem.toString()).append('\n');
		}

		if (cause.getCause() != null) {
			addRootCause(buf, cause.getCause());
		}
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer("");
		buf.append(SlcTestUtils.statusToString(status));
		if (status == PASSED || status == FAILED) {
			buf.append(' ');
		} else if (status == ERROR) {
			buf.append("  ");
		}
		buf.append(message);
		return buf.toString();
	}

	/** @deprecated */
	Long getTid() {
		return tid;
	}

	/** @deprecated */
	void setTid(Long tid) {
		this.tid = tid;
	}

	public String getTestRunUuid() {
		return testRunUuid;
	}

	/** For ORM */
	public void setTestRunUuid(String testRunUuid) {
		this.testRunUuid = testRunUuid;
	}

	public void notifyTestRun(TestRun testRun) {
		testRunUuid = testRun.getUuid();
	}

	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}

}
