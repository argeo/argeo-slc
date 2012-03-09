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
