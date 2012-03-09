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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
	
	public Set getDynamicRefKeys() {
		return dynamicRefs.keySet();
	}
}
