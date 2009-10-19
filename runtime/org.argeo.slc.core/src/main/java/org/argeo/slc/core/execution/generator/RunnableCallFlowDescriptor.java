package org.argeo.slc.core.execution.generator;

import java.util.ArrayList;
import java.util.List;

/**
 * Storage Class for information required to
 * instantiate a <code>RunnableCallFlow</code>:
 * bean name of the flow, 
 * path of the flow 
 * and list of <code>RunnableCall</code>. 
 *
 */
public class RunnableCallFlowDescriptor {
	
	/**
	 * Bean name of the flow to instantiate
	 */
	private String beanName;
	
	/**
	 * Path of the flow to instantiate
	 */
	private String path;
	
	/**
	 * List of <code>RunnableCall</code> 
	 */
	private List<RunnableCall> runnableCalls = new ArrayList<RunnableCall>();

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<RunnableCall> getRunnableCalls() {
		return runnableCalls;
	}

	public void setRunnableCalls(List<RunnableCall> runnableCalls) {
		this.runnableCalls = runnableCalls;
	}
	
}
