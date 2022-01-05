package org.argeo.slc.core.execution.tasks;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.argeo.api.cms.CmsLog;
import org.argeo.slc.SlcException;
import org.springframework.core.io.Resource;

public class Echo implements Runnable {
	private final static CmsLog defaultLog = CmsLog.getLog(Echo.class);
	private Resource writeTo = null;

	private CmsLog log;
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

	private CmsLog log() {
		return log != null ? log : defaultLog;
	}

	public void setMessage(Object message) {
		this.message = message;
	}

	public void setWriteTo(Resource writeTo) {
		this.writeTo = writeTo;
	}

}
