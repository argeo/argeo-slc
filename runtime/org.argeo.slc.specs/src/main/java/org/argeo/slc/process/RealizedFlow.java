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

package org.argeo.slc.process;

import java.io.Serializable;

import org.argeo.slc.build.BasicNameVersion;
import org.argeo.slc.build.NameVersion;
import org.argeo.slc.execution.ExecutionFlowDescriptor;
import org.argeo.slc.execution.ExecutionSpec;

public class RealizedFlow implements Serializable {
	private static final long serialVersionUID = 1L;

	private String moduleName;
	private String moduleVersion;
	private ExecutionFlowDescriptor flowDescriptor;
	private ExecutionSpec executionSpec;

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public NameVersion getModuleNameVersion() {
		return new BasicNameVersion(getModuleName(), getModuleVersion());
	}

	public String getModuleVersion() {
		return moduleVersion;
	}

	public void setModuleVersion(String moduleVersion) {
		this.moduleVersion = moduleVersion;
	}

	public ExecutionFlowDescriptor getFlowDescriptor() {
		return flowDescriptor;
	}

	public void setFlowDescriptor(ExecutionFlowDescriptor flowDescriptor) {
		this.flowDescriptor = flowDescriptor;
	}

	public ExecutionSpec getExecutionSpec() {
		return executionSpec;
	}

	public void setExecutionSpec(ExecutionSpec executionSpec) {
		this.executionSpec = executionSpec;
	}
}
