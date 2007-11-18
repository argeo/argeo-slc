package org.argeo.slc.core.test;

/**
 * <p>
 * Basic implementation of a result part, implementing the standard three status
 * approach for test results.
 * </p>
 * 
 * @see TestStatus
 */
public class SimpleResultPart implements TestResultPart, TestStatus {

	/** For ORM */
	private Long tid;

	private Integer status;
	private String message;
	private Throwable exception;

	public SimpleResultPart() {
	}

	public SimpleResultPart(Integer status, String message) {
		this(status, message, null);
	}

	public SimpleResultPart(Integer status, String message, Throwable exception) {
		this.status = status;
		this.message = message;
		this.exception = exception;
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

	public Throwable getException() {
		return exception;
	}

	public void setException(Throwable exception) {
		this.exception = exception;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer("");
		if (status == PASSED) {
			buf.append("PASSED ");
		} else if (status == FAILED) {
			buf.append("FAILED ");
		} else if (status == ERROR) {
			buf.append("ERROR  ");
		}
		buf.append(message);
		if (exception != null) {
			buf.append("(").append(exception.getMessage()).append(")");
		}
		return buf.toString();
	}

	Long getTid() {
		return tid;
	}

	void setTid(Long tid) {
		this.tid = tid;
	}

}
