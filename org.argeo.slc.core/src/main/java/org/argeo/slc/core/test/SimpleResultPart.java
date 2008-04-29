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

	/** The status. Default to ERROR since it should always be explicitely set. */
	private Integer status = ERROR;
	private String message;
	private Exception exception;

	public SimpleResultPart() {
	}

	public SimpleResultPart(Integer status, String message) {
		this(status, message, null);
	}

	public SimpleResultPart(Integer status, String message, Exception exception) {
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

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
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
