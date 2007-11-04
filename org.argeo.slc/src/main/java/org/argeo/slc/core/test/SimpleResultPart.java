package org.argeo.slc.core.test;

/**
 * <p>
 * Basic implementation of a result part, implementing the standard three status
 * approach for test results.
 * </p>
 * <p>
 * <ul>
 * <li>{@link #PASSED}: the test succeeded</li>
 * <li>{@link #FAILED}: the test could run, but did not reach the expected
 * result</li>
 * <li>{@link #ERROR}: an error during the test run prevented to get a
 * significant information on the tested system.</li>
 * </ul>
 * </p>
 */
public class SimpleResultPart implements TestResultPart {

	/** The flag for a passed test: 1 */
	public final static int PASSED = 1;
	/** The flag for a failed test: 2 */
	public final static int FAILED = 2;
	/** The flag for a test which could not properly run because of an error: 3 */
	public final static int ERROR = 3;

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
