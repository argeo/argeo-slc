package org.argeo.slc.detached;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

public class DetachedContextImpl implements DetachedContext {
	private List executedPaths = new Vector();
	private String currentPath;
	private Map dynamicRefs = new TreeMap();

	public String getCurrentPath() {
		return currentPath;
	}

	public void setDynamicRef(String ref, Object obj) {
		dynamicRefs.put(ref, obj);
	}

	public Object getDynamicRef(String ref) {
		if (dynamicRefs.containsKey(ref))
			return dynamicRefs.get(ref);
		else
			return null;
	}

	public List getExecutedPaths() {
		return new ArrayList(executedPaths);
	}

	public String toString() {
		StringBuffer buf = new StringBuffer(getClass().getName());
		buf.append(" currentPath=").append(currentPath);
		buf.append(" executedPaths=").append(executedPaths);
		return buf.toString();
	}
}
