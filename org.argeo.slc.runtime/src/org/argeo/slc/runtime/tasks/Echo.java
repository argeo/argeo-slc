package org.argeo.slc.runtime.tasks;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.file.Files;
import java.nio.file.Path;

import org.argeo.api.slc.SlcException;

public class Echo implements Runnable {
	private final static Logger defaultLogger = System.getLogger(Echo.class.getName());
	private Path writeTo = null;

	private Logger log;
	private Object message;

	public void run() {
		log().log(Level.INFO, message);

		if (writeTo != null) {
			try {
				log().log(Level.DEBUG, () -> "Write to " + writeTo);
				if (message != null)
					Files.writeString(writeTo, message.toString());
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
