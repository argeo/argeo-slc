package org.argeo.slc.runtime.tasks;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;

public class Echo implements Runnable {
	private final static Log defaultLog = LogFactory.getLog(Echo.class);
	private Path writeTo = null;

	private Log log;
	private Object message;

	public void run() {
		log().info(message);

		if (writeTo != null) {
			try {
				File file = writeTo.toFile();
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

	public void setWriteTo(Path writeTo) {
		this.writeTo = writeTo;
	}

}
