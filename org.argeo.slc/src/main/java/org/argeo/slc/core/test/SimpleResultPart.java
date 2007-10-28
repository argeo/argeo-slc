package org.argeo.slc.core.test;

public class SimpleResultPart implements TestResultPart {

	public final static Integer PASSED = 1;
	public final static Integer FAILED = 2;
	public final static Integer ERROR = 3;

	/** For ORM */
	private Long tid;
	
	private Integer status;
	private String message;
	private Throwable exception;

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
