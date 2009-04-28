package org.argeo.slc.msg;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.argeo.slc.SlcException;

/** Answer to an execution of a remote service which performed changes. */
public class ExecutionAnswer {
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
