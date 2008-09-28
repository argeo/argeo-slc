package org.argeo.slc.autoui;

import java.util.List;

public interface DetachedContext {
	public Object getDynamicRef(String ref);

	public void setDynamicRef(String ref, Object obj);

	public String getCurrentPath();

	public List getExecutedPaths();
}
