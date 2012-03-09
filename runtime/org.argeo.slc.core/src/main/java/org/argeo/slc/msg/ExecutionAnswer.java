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
package org.argeo.slc.msg;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.argeo.slc.SlcException;

/** Answer to an execution of a remote service which performed changes. */
public class ExecutionAnswer implements Serializable {
	private static final long serialVersionUID = -3268867743181316160L;
	public final static String OK = "OK";
	public final static String ERROR = "ERROR";

	private String status = OK;
	private String message = "";

	/** Canonical constructor */
	public ExecutionAnswer(String status, String message) {
		setStatus(status);
		if (message == null)
			throw new SlcException("Message cannot be null");
		this.message = message;
	}

	/** Empty constructor */
	public ExecutionAnswer() {
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		if (status == null || (!status.equals(OK) && !status.equals(ERROR)))
			throw new SlcException("Bad status format: " + status);
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Boolean isOk() {
		return status.equals(OK);
	}

	public Boolean isError() {
		return status.equals(ERROR);
	}

	public static ExecutionAnswer error(String message) {
		return new ExecutionAnswer(ERROR, message);
	}

	public static ExecutionAnswer error(Throwable e) {
		StringWriter writer = new StringWriter();
		try {
			e.printStackTrace(new PrintWriter(writer));
			return new ExecutionAnswer(ERROR, writer.toString());
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}

	public static ExecutionAnswer ok(String message) {
		return new ExecutionAnswer(OK, message);
	}

}
