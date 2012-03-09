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
package org.argeo.slc.execution;

import java.util.ArrayList;
import java.util.List;

import org.argeo.slc.deploy.ModuleDescriptor;

/** Describes the information required to launch a flow */
public class ExecutionModuleDescriptor extends ModuleDescriptor {
	private static final long serialVersionUID = -2394473464513029512L;
	private List<ExecutionSpec> executionSpecs = new ArrayList<ExecutionSpec>();
	private List<ExecutionFlowDescriptor> executionFlows = new ArrayList<ExecutionFlowDescriptor>();

	public List<ExecutionSpec> getExecutionSpecs() {
		return executionSpecs;
	}

	public List<ExecutionFlowDescriptor> getExecutionFlows() {
		return executionFlows;
	}

	public void setExecutionSpecs(List<ExecutionSpec> executionSpecs) {
		this.executionSpecs = executionSpecs;
	}

	public void setExecutionFlows(List<ExecutionFlowDescriptor> executionFlows) {
		this.executionFlows = executionFlows;
	}
}
