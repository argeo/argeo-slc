package org.argeo.slc.detached;

import java.util.Properties;

public class DetachedAnswer implements DetachedCommunication {
	static final long serialVersionUID = 1l;

	public static int UNKOWN = -1;
	public static int PROCESSED = 0;
	public static int ERROR = 1;
	public static int SKIPPED = 2;
	public static int CLOSED_SESSION = 10;

	private Properties properties = new Properties();
	private int status = UNKOWN;
	private String log;
	private String uuid;

	public DetachedAnswer() {

	}

	public DetachedAnswer(DetachedRequest request) {
		uuid = request.getUuid();
	}

	public DetachedAnswer(DetachedRequest request, String message) {
		this(request);
		log = message;
		status = PROCESSED;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties outputParameters) {
		this.properties = outputParameters;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int outputStatus) {
		this.status = outputStatus;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

}
