package org.argeo.slc.core.execution.tasks;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.springframework.core.io.Resource;

public class Echo implements Runnable {
	private final static Log defaultLog = LogFactory.getLog(Echo.class);
	private Resource writeTo = null;

	private Log log;
	private Object message;

	public void run() {
		log().info(message);

		if (writeTo != null) {
			try {
				File file = writeTo.getFile();
				if (log().isDebugEnabled())
					log().debug("Write to " + file);
				if (message != null)
					FileUtils.writeStringToFile(file, message.toString());
			} catch (IOException e) {
				throw new SlcException("Could not write to " + writeTo, e);
			}
		}
	}

	private Log log() {
		return log != null ? log : defaultLog;
	}

	public void setMessage(Object message) {
		this.message = message;
	}

	public void setWriteTo(Resource writeTo) {
		this.writeTo = writeTo;
	}

}
