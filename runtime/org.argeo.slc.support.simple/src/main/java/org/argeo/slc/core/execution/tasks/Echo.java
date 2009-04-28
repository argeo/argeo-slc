package org.argeo.slc.core.execution.tasks;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Echo implements Runnable {
	private final static Log defaultLog = LogFactory.getLog(Echo.class);

	private Log log;
	private String message;

	public void run() {
		log().info(message);
	}

	protected Log log() {
		return log != null ? log : defaultLog;
	}

	public void setLog(Log log) {
		this.log = log;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
