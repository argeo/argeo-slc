package org.argeo.slc.detached;

import java.util.Properties;

public class DetachedRequest implements DetachedCommunication {
	static final long serialVersionUID = 1l;

	private String uuid;
	private Properties properties = new Properties();
	private String ref;
	private String path = "";

	public DetachedRequest() {

	}

	public DetachedRequest(String uuid) {
		this.uuid = uuid;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties inputParameters) {
		this.properties = inputParameters;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String stepRef) {
		this.ref = stepRef;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
