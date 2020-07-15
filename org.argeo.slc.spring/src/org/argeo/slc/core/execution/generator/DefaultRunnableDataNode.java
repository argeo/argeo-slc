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
