package org.argeo.slc.msg;

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

}
