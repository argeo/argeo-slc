/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
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

package org.argeo.slc.core.execution.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of <code>RunnableDataNode</code>
 *
 */
public class DefaultRunnableDataNode implements RunnableDataNode {

	private List<RunnableDataNode> children = new ArrayList<RunnableDataNode>();
	
	private RunnableDataNode parent;
	
	/**
	 * Data of the RunnableDataNode. Does not contain
	 * parent data.
	 */
	private Map<String, Object> properData = new HashMap<String, Object>();
	
	private String path;
	
	private String beanName;

	public boolean isLeaf() {
		return children.size() == 0;
	}
	
	public List<RunnableDataNode> getChildren() {
		return children;
	}

	public void addChild(RunnableDataNode child) {
		child.setParent(this);
		children.add(child);
	}
	
	public Map<String, Object> getData() {
		Map<String, Object> data = new HashMap<String, Object>();
		if(parent != null) {
			Map<String, Object> parentData = parent.getData();
			if(parentData != null) {
				data.putAll(parentData);
			}
		}
		// entries defined in parentData can be overridden
		// in properData
		if(properData != null) {
			data.putAll(properData);
		}
		return data;
	}

	public Map<String, Object> getProperData() {
		return properData;
	}

	public void setProperData(Map<String, Object> properData) {
		this.properData = properData;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public void setParent(RunnableDataNode parent) {
		this.parent = parent;
	}

	public RunnableDataNode getParent() {
		return parent;
	}

}
