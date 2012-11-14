/*
 * Copyright (C) 2007-2012 Argeo GmbH
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

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.core.execution.DefaultExecutionSpec;
import org.argeo.slc.execution.ExecutionContext;
import org.argeo.slc.execution.ExecutionFlow;
import org.argeo.slc.execution.ExecutionSpec;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Execution Flow calling a list of <code>Runnable</code> (identified by their
 * bean name in the Spring Application Context) after configuring the Execution
 * context and a Map potentially shared by the called <code>Runnable</code>
 * 
 */
public class RunnableCallFlow implements ExecutionFlow, ApplicationContextAware {

	private final static Log log = LogFactory.getLog(RunnableCallFlow.class);

	/**
	 * Key in the execution context for the index of the call (e.g. 0 for the
	 * first runnable called, ...)
	 */
	public final static String VAR_CALL_INDEX = "slcVar.runnableCallFlow.callIndex";

	/**
	 * Name of the flow. Also bean name
	 */
	private String name;

	/**
	 * Path of the flow
	 */
	private String path;

	/**
	 * Whether an exception in a <code>Runnable</code> shall stop the execution
	 * of the flow
	 */
	private Boolean failOnError = true;

	/**
	 * List of <code>Runnable</code> to call, with bean name, execution
	 * variables and context values
	 */
	private List<RunnableCall> runnableCalls;

	/**
	 * Map potentially referenced by called flows. Updated with the context
	 * values of a Runnable before calling it.
	 */
	private Map<String, Object> sharedContextValuesMap;

	/**
	 * ExecutionSpec of the flow. Does not contain any attribute.
	 */
	private ExecutionSpec executionSpec = new DefaultExecutionSpec();

	/**
	 * Reference to the ExecutionContext
	 */
	private ExecutionContext executionContext;

	/**
	 * Reference to the Spring <code>ApplicationContext</code>. Set via
	 * <code>setApplicationContext</code>, the class implementing
	 * <code>ApplicationContextAware</code>
	 */
	private ApplicationContext applicationContext;

	/**
	 * Runs a <code>Runnable</code> after configuring the Execution Context and
	 * <code>sharedContextValuesMap</code>
	 * 
	 * @param runnable
	 *            the <code>Runnable</code> to call
	 * @param executionVariables
	 *            the variables to add to the <code>ExecutionContext</code>
	 * @param contextValues
	 *            the variables to add to <code>sharedContextValuesMap</code>
	 * @param callIndex
	 *            index of the call (0 for the first called
	 *            <code>Runnable</code>) set as variable of the
	 *            <code>ExecutionContext</code>
	 */
	private void run(Runnable runnable, Map<String, Object> executionVariables,
			Map<String, Object> contextValues, int callIndex) {
		// add all variables to the Execution Context
		for (Map.Entry<String, Object> entry : executionVariables.entrySet()) {
			executionContext.setVariable(entry.getKey(), entry.getValue());
		}

		// add call Index Variable
		executionContext.setVariable(VAR_CALL_INDEX, callIndex);

		// clear sharedContextValues and add all values of contextValues
		if (sharedContextValuesMap != null) {
			sharedContextValuesMap.clear();
			sharedContextValuesMap.putAll(contextValues);
		}

		// then run the runnable
		doExecuteRunnable(runnable);
	}

	public void doExecuteRunnable(Runnable runnable) {
		runnable.run();
	}

	/**
	 * Executes the flow. For each <code>RunnableCall</code>, the corresponding
	 * flow is retrieved from the Spring Application Context, the
	 * <code>ExecutionContext</code> and <code>sharedContextValuesMap</code> are
	 * configured and the <code>Runnable</code> is called.
	 */
	public void run() {
		if (applicationContext == null) {
			throw new SlcException("No ApplicationContext defined");
		}

		try {
			for (int callIndex = 0; callIndex < runnableCalls.size(); ++callIndex) {
				RunnableCall runnableCall = runnableCalls.get(callIndex);
				Object bean = applicationContext.getBean(runnableCall
						.getBeanName(), Runnable.class);
				if (log.isDebugEnabled())
					log.debug("Running flow '" + runnableCall.getBeanName()
							+ "'");
				run((Runnable) bean, runnableCall.getExecutionVariables(),
						runnableCall.getContextValues(), callIndex);
			}
		} catch (RuntimeException e) {
			if (failOnError)
				throw e;
			else {
				log.error("Execution flow failed,"
						+ " but process did not fail"
						+ " because failOnError property"
						+ " is set to false: " + e);
				if (log.isTraceEnabled())
					e.printStackTrace();
			}
		}
	}

	@Override
	public String toString() {
		return new StringBuffer("RunnableCallFlow ").append(name).toString();
	}	
	
	public ExecutionSpec getExecutionSpec() {
		return executionSpec;
	}

	public String getName() {
		return name;
	}

	public Object getParameter(String key) {
		throw new SlcException("RunnableCallFlow have no parameters");
	}

	public String getPath() {
		return path;
	}

	public Boolean isSetAsParameter(String key) {
		// The ExecutionSpec having no attribute,
		// always return false
		return false;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setExecutionContext(ExecutionContext executionContext) {
		this.executionContext = executionContext;
	}

	public void setRunnableCalls(List<RunnableCall> runnableCalls) {
		this.runnableCalls = runnableCalls;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public void setSharedContextValuesMap(Map<String, Object> contextValues) {
		this.sharedContextValuesMap = contextValues;
	}

	public void setFailOnError(Boolean failOnError) {
		this.failOnError = failOnError;
	}

}
