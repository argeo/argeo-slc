package org.argeo.slc.autoui.swingtest.rmi;

public interface AutoUiContext {
	public Object getLocalRef(String key);

	public void setLocalRef(String key, Object ref);
}
