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
package org.argeo.slc.core.execution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.execution.ExecutionFlow;
import org.argeo.slc.execution.ExecutionSpec;
import org.argeo.slc.execution.ExecutionSpecAttribute;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.validation.MapBindingResult;

/** Default implementation of an execution flow. */
public class DefaultExecutionFlow implements ExecutionFlow, BeanNameAware {
	private final static Log log = LogFactory
			.getLog(DefaultExecutionFlow.class);

	private final ExecutionSpec executionSpec;
	private String name = null;
	private Map<String, Object> parameters = new HashMap<String, Object>();
	private List<Runnable> executables = new ArrayList<Runnable>();

	private Boolean failOnError = true;

	public DefaultExecutionFlow() {
		this.executionSpec = new DefaultExecutionSpec();
	}

	public DefaultExecutionFlow(ExecutionSpec executionSpec) {
		this.executionSpec = executionSpec;
	}

	public DefaultExecutionFlow(ExecutionSpec executionSpec,
			Map<String, Object> parameters) {
		// be sure to have an execution spec
		this.executionSpec = (executionSpec == null) ? new DefaultExecutionSpec()
				: executionSpec;

		// only parameters contained in the executionSpec can be set
		for (String parameter : parameters.keySet()) {
			if (!executionSpec.getAttributes().containsKey(parameter)) {
				throw new SlcException("Parameter " + parameter
						+ " is not defined in the ExecutionSpec");
			}
		}

		// set the parameters
		this.parameters.putAll(parameters);

		// check that all the required parameters are defined
		MapBindingResult errors = new MapBindingResult(parameters, "execution#"
				+ getName());
		for (String key : executionSpec.getAttributes().keySet()) {
			ExecutionSpecAttribute attr = executionSpec.getAttributes()
					.get(key);

			if (attr.getIsImmutable() && !isSetAsParameter(key)) {
				errors.rejectValue(key, "Immutable but not set");
				break;
			}

			if (attr.getIsConstant() && !isSetAsParameter(key)) {
				errors.rejectValue(key, "Constant but not set as parameter");
				break;
			}

			if (attr.getIsHidden() && !isSetAsParameter(key)) {
				errors.rejectValue(key, "Hidden but not set as parameter");
				break;
			}
		}

		if (errors.hasErrors())
			throw new SlcException("Could not prepare execution flow: "
					+ errors.toString());

	}

	public void run() {
		try {
			for (Runnable executable : executables) {
				if (Thread.interrupted()) {
					log.error("Flow '" + getName() + "' killed before '"
							+ executable + "'");
					Thread.currentThread().interrupt();
					return;
					// throw new ThreadDeath();
				}
				this.doExecuteRunnable(executable);
			}
		} catch (RuntimeException e) {
			if (Thread.interrupted()) {
				log.error("Flow '" + getName()
						+ "' killed while receiving an unrelated exception", e);
				Thread.currentThread().interrupt();
				return;
				// throw new ThreadDeath();
			}
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

	public void doExecuteRunnable(Runnable runnable) {
		runnable.run();
	}

	public void setBeanName(String name) {
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
			throw new SlcException("Key " + parameterName
					+ " is not defined in the specifications of " + toString());
		}

		if (parameters.containsKey(parameterName)) {
			Object paramValue = parameters.get(parameterName);
			return paramValue;
		} else {
			if (specAttr.getValue() != null) {
				return specAttr.getValue();
			}
		}
		throw new SlcException("Key " + parameterName
				+ " is not set as parameter in " + toString());
	}

	public Boolean isSetAsParameter(String key) {
		return parameters.containsKey(key)
				|| (executionSpec.getAttributes().containsKey(key) && executionSpec
						.getAttributes().get(key).getValue() != null);
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

	/** @deprecated does nothing */
	@Deprecated
	public void setPath(String path) {
	}

	public Boolean getFailOnError() {
		return failOnError;
	}

	public void setFailOnError(Boolean failOnError) {
		this.failOnError = failOnError;
	}

}
