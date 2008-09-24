package org.argeo.slc.autoui.rmi;

public interface AutoUiContext {
	public Object getLocalRef(String key);

	public void setLocalRef(String key, Object ref);
}
