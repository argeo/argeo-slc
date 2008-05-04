package org.argeo.slc.core.test;


/**
 * <p>
 * Basic implementation of a result part, implementing the standard three status
 * approach for test results.
 * </p>
 * 
 * @see TestStatus
 */
public class SimpleResultPart implements TestResultPart, TestStatus,
		TestRunAware {

	/** @deprecated */
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

		//this.exceptionMessage = exception.getMessage();

		StringBuffer buf = new StringBuffer("");
		buf.append(exception.getMessage());
		buf.append('\n');
		for(StackTraceElement elem : exception.getStackTrace()){
			buf.append(elem.toString()).append('\n');
		}
		
		this.exceptionMessage = buf.toString();
		
/*		
		StringWriter writer = null;
		StringReader reader = null;
		try {
			writer = new StringWriter();
			exception.printStackTrace(new PrintWriter(writer));
			reader = new StringReader(writer.toString());
			exceptionStackLines = new Vector<String>(IOUtils.readLines(reader));
		} catch (IOException e) {
			// silent
		} finally {
			IOUtils.closeQuietly(writer);
			IOUtils.closeQuietly(reader);
		}*/
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
