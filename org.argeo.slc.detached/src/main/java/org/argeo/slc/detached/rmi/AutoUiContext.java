package org.argeo.slc.detached.rmi;

public interface AutoUiContext {
	public Object getLocalRef(String key);

	public void setLocalRef(String key, Object ref);
}
