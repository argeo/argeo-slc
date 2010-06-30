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

import java.util.Map;

/**
 * Storage class for information required to call a flow 
 * of the Spring execution context: 
 * bean name of the flow,
 * variables to add to the Execution Context before the call 
 * and variables (context values) to add to a Map 
 * potentially referenced by the called flow 
 */
public class RunnableCall {
	
	/**
	 * Bean name of the flow to call
	 */
	private String beanName;
	
	/**
	 * Variables to add to the execution context before the call
	 */
	private Map<String, Object> executionVariables;
	
	/**
	 * Variables to add to the Map potentially referenced by
	 * the called flow
	 */
	private Map<String, Object> contextValues;

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public Map<String, Object> getExecutionVariables() {
		return executionVariables;
	}

	public void setExecutionVariables(Map<String, Object> executionVariables) {
		this.executionVariables = executionVariables;
	}

	public Map<String, Object> getContextValues() {
		return contextValues;
	}

	public void setContextValues(Map<String, Object> contextValues) {
		this.contextValues = contextValues;
	}

}
