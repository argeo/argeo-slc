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
	/** For ORM */
	private Long tid;

	private TestStatus status;
	private String message;
	private Throwable exception;

	
	/** Empty constructor for ORM */
	public SimpleResultPart(){
		
	}
	
	public SimpleResultPart(TestStatus status, String message,
			Throwable exception) {
		super();
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

	public void setStatus(TestStatus status) {
		this.status = status;
	}

	public TestStatus getStatus() {
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
		buf.append(status).append(" ");
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
