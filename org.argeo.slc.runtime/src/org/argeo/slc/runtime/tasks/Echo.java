package org.argeo.slc.runtime.tasks;

import java.io.File;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.argeo.slc.SlcException;

public class Echo implements Runnable {
	private final static Logger defaultLogger = System.getLogger(Echo.class.getName());
	private Path writeTo = null;

	private Logger log;
	private Object message;

	public void run() {
		log().log(Level.INFO, message);

		if (writeTo != null) {
			try {
				File file = writeTo.toFile();

				log().log(Level.DEBUG, () -> "Write to " + file);
				if (message != null)
					FileUtils.writeStringToFile(file, message.toString());
			} catch (IOException e) {
				throw new SlcException("Could not write to " + writeTo, e);
			}
		}
	}

	private Logger log() {
		return log != null ? log : defaultLogger;
	}

	public void setMessage(Object message) {
		this.message = message;
	}

	public void setWriteTo(Path writeTo) {
		this.writeTo = writeTo;
	}

}
