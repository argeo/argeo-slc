package org.argeo.slc.detached;

import java.util.Properties;

/** A request sent to the detached server. */
public class DetachedRequest implements DetachedCommunication {
	static final long serialVersionUID = 1l;

	private String uuid;
	private Properties properties = new Properties();
	private String ref;
	private String path = "";

	private Object cachedObject = null;

	public DetachedRequest() {

	}

	public DetachedRequest(String uuid) {
		this.uuid = uuid;
	}

	/** The properties configuring this request. */
	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties inputParameters) {
		this.properties = inputParameters;
	}

	/**
	 * A reference to the underlying implementation which will process the
	 * request.
	 */
	public String getRef() {
		return ref;
	}

	public void setRef(String stepRef) {
		this.ref = stepRef;
	}

	/** A path identifying the request within its source context. */
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	/** The unique identifier of this request. */
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer("detached request for ref ");
		buf.append(ref);
		buf.append(" #").append(uuid);
		buf.append(" cachedObject=").append((cachedObject != null));
		buf.append(" path=").append(path);
		buf.append(" properties=").append(properties);
		return buf.toString();
	}

	/**
	 * Optimization. Allows the driver to eagerly cache the object in the
	 * request, in order to relieve the detached server of the task to look for
	 * it. No implementation should rely on this to be set.
	 */
	public Object getCachedObject() {
		return cachedObject;
	}

	public void setCachedObject(Object cachedObject) {
		this.cachedObject = cachedObject;
	}

}
