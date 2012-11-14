/*
 * Copyright (C) 2007-2012 Argeo GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
