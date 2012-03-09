/*
 * Copyright (C) 2007-2012 Mathieu Baudier
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
package org.argeo.slc.lib.detached;

import java.util.Properties;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.detached.DetachedAnswer;
import org.argeo.slc.detached.DetachedClient;
import org.argeo.slc.detached.DetachedRequest;

public class DetachedAdminTask implements Runnable {
	private final static Log log = LogFactory.getLog(DetachedAdminTask.class);

	private String action;
	private DetachedClient client;
	private Properties properties;

	public void run() {
		// Prepare and send request
		DetachedRequest request = new DetachedRequest(UUID.randomUUID()
				.toString());
		request.setRef(action);

		if (properties != null) {
			request.setProperties(properties);
		}

		try {
			client.sendRequest(request);
			DetachedAnswer answer = client.receiveAnswer();
			if (answer.getStatus() == DetachedAnswer.ERROR)
				throw new SlcException("Error when executing request "
						+ answer.getUuid() + ": " + answer.getLog());
			else
				log.info("Admin answer: " + answer.getLog());
		} catch (Exception e) {
			throw new SlcException("Could not send request.", e);
		}
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setClient(DetachedClient detachedClient) {
		this.client = detachedClient;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

}
