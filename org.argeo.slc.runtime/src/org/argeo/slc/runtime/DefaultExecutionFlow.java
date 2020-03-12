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
package org.argeo.slc.runtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.execution.ExecutionContext;
import org.argeo.slc.execution.ExecutionFlow;
import org.argeo.slc.execution.ExecutionSpec;
import org.argeo.slc.execution.ExecutionSpecAttribute;

/** Default implementation of an execution flow. */
public class DefaultExecutionFlow implements ExecutionFlow {
	private final static Log log = LogFactory.getLog(DefaultExecutionFlow.class);

	private final ExecutionSpec executionSpec;
	private String name = null;
	private Map<String, Object> parameters = new HashMap<String, Object>();
	private List<Runnable> executables = new ArrayList<Runnable>();

	private String path;

	private Boolean failOnError = true;

	// Only needed if stacked execution flows are used
	private ExecutionContext executionContext = null;

	public DefaultExecutionFlow() {
		this.executionSpec = new DefaultExecutionSpec();
	}

	public DefaultExecutionFlow(ExecutionSpec executionSpec) {
		this.executionSpec = executionSpec;
	}

	public DefaultExecutionFlow(ExecutionSpec executionSpec, Map<String, Object> parameters) {
		// be sure to have an execution spec
		this.executionSpec = (executionSpec == null) ? new DefaultExecutionSpec() : executionSpec;

		// only parameters contained in the executionSpec can be set
		for (String parameter : parameters.keySet()) {
			if (!executionSpec.getAttributes().containsKey(parameter)) {
				throw new SlcException("Parameter " + parameter + " is not defined in the ExecutionSpec");
			}
		}

		// set the parameters
		this.parameters.putAll(parameters);

		// check that all the required parameters are defined
//		MapBindingResult errors = new MapBindingResult(parameters, "execution#"
//				+ getName());
		Map<String, String> errors = new HashMap<>();
		for (String key : executionSpec.getAttributes().keySet()) {
			ExecutionSpecAttribute attr = executionSpec.getAttributes().get(key);

			if (attr.getIsImmutable() && !isSetAsParameter(key)) {
				errors.put(key, "Immutable but not set");
				break;
			}

			if (attr.getIsConstant() && !isSetAsParameter(key)) {
				errors.put(key, "Constant but not set as parameter");
				break;
			}

			if (attr.getIsHidden() && !isSetAsParameter(key)) {
				errors.put(key, "Hidden but not set as parameter");
				break;
			}
		}

		if (!errors.isEmpty())
			throw new SlcException("Could not prepare execution flow: " + errors.toString());

	}

	public void run() {
		try {
			for (Runnable executable : executables) {
				if (Thread.interrupted()) {
					log.error("Flow '" + getName() + "' killed before '" + executable + "'");
					Thread.currentThread().interrupt();
					return;
					// throw new ThreadDeath();
				}
				this.doExecuteRunnable(executable);
			}
		} catch (RuntimeException e) {
			if (Thread.interrupted()) {
				log.error("Flow '" + getName() + "' killed while receiving an unrelated exception", e);
				Thread.currentThread().interrupt();
				return;
				// throw new ThreadDeath();
			}
			if (failOnError)
				throw e;
			else {
				log.error("Execution flow failed," + " but process did not fail" + " because failOnError property"
						+ " is set to false: " + e);
				if (log.isTraceEnabled())
					e.printStackTrace();
			}
		}
	}

	/**
	 * List sub-runnables that would be executed if run() method would be called.
	 */
	public Iterator<Runnable> runnables() {
		return executables.iterator();
	}

	/**
	 * If there is one and only one runnable wrapped return it, throw an exeception
	 * otherwise.
	 */
	public Runnable getRunnable() {
		if (executables.size() == 1)
			return executables.get(0);
		else
			throw new SlcException("There are " + executables.size() + " runnables in flow " + getName());
	}

	public void doExecuteRunnable(Runnable runnable) {
		try {
			if (executionContext != null)
				if (runnable instanceof ExecutionFlow)
					executionContext.beforeFlow((ExecutionFlow) runnable);
			runnable.run();
		} finally {
			if (executionContext != null)
				if (runnable instanceof ExecutionFlow)
					executionContext.afterFlow((ExecutionFlow) runnable);
		}
	}

	public void init() throws Exception {
		if (path == null) {
			if (name.charAt(0) == '/') {
				path = name.substring(0, name.lastIndexOf('/'));
			}
		}

		if (path != null) {
			for (Runnable executable : executables) {
				if (executable instanceof DefaultExecutionFlow) {
					// so we don't need to have DefaultExecutionFlow
					// implementing StructureAware
					// FIXME: probably has side effects
					DefaultExecutionFlow flow = (DefaultExecutionFlow) executable;
					String newPath = path + '/' + flow.getName();
					flow.setPath(newPath);
					log.warn(newPath + " was forcibly set on " + flow);
				}
			}
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setExecutables(List<Runnable> executables) {
		this.executables = executables;
	}

	public void setParameters(Map<String, Object> attributes) {
		this.parameters = attributes;
	}

	public String getName() {
		return name;
	}

	public ExecutionSpec getExecutionSpec() {
		return executionSpec;
	}

	public Object getParameter(String parameterName) {
		// Verify that there is a spec attribute
		ExecutionSpecAttribute specAttr = null;
		if (executionSpec.getAttributes().containsKey(parameterName)) {
			specAttr = executionSpec.getAttributes().get(parameterName);
		} else {
			throw new SlcException("Key " + parameterName + " is not defined in the specifications of " + toString());
		}

		if (parameters.containsKey(parameterName)) {
			Object paramValue = parameters.get(parameterName);
			return paramValue;
		} else {
			if (specAttr.getValue() != null) {
				return specAttr.getValue();
			}
		}
		throw new SlcException("Key " + parameterName + " is not set as parameter in " + toString());
	}

	public Boolean isSetAsParameter(String key) {
		return parameters.containsKey(key) || (executionSpec.getAttributes().containsKey(key)
				&& executionSpec.getAttributes().get(key).getValue() != null);
	}

	@Override
	public String toString() {
		return new StringBuffer("Execution flow ").append(name).toString();
	}

	@Override
	public boolean equals(Object obj) {
		return ((ExecutionFlow) obj).getName().equals(name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Boolean getFailOnError() {
		return failOnError;
	}

	public void setFailOnError(Boolean failOnError) {
		this.failOnError = failOnError;
	}

	public void setExecutionContext(ExecutionContext executionContext) {
		this.executionContext = executionContext;
	}

}