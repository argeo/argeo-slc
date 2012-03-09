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
package org.argeo.slc.detached;

import java.util.Properties;

/** An answer returned by the detached server. Always related to a request. */
public class DetachedAnswer implements DetachedCommunication {
	static final long serialVersionUID = 1l;

	public final static int UNKOWN = -1;
	public final static int PROCESSED = 0;
	public final static int ERROR = 1;
	public final static int SKIPPED = 2;
	public final static int CLOSED_SESSION = 10;

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

	public void addToLog(String msg) {
		this.log = new StringBuffer(this.log).append(msg).toString();
	}

	/** The unique identifier of this answer. */
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getStatusAsString() {
		return convertStatus(getStatus());
	}

	public static String convertStatus(int status) {
		switch (status) {
		case UNKOWN:
			return "UNKOWN";
		case PROCESSED:
			return "PROCESSED";
		case SKIPPED:
			return "SKIPPED";
		case ERROR:
			return "ERROR";
		case CLOSED_SESSION:
			return "CLOSED_SESSION";
		default:
			throw new DetachedException("Unkown status " + status);
		}
	}

	public String toString() {
		StringBuffer buf = new StringBuffer("detached answer ");
		buf.append('#').append(uuid);
		buf.append(" status=").append(convertStatus(status));
		buf.append(" properties=").append(properties);
		buf.append(" log=").append(log);
		return buf.toString();
	}
}
