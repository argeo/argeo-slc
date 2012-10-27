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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.argeo.slc.BasicNameVersion;
import org.argeo.slc.NameVersion;

/** A fully configured execution flow, ready to be executed. */
public class RealizedFlow implements Serializable {
	private static final long serialVersionUID = 1L;

	private String moduleName;
	private String moduleVersion;
	private ExecutionFlowDescriptor flowDescriptor;

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

	/** Create a simple realized flow */
	public static RealizedFlow create(String module, String version,
			String flowName, Map<String, String> args) {
		final RealizedFlow realizedFlow = new RealizedFlow();
		realizedFlow.setModuleName(module);
		// TODO deal with version
		if (version == null)
			version = "0.0.0";
		realizedFlow.setModuleVersion(version);
		ExecutionFlowDescriptor efd = new ExecutionFlowDescriptor();
		efd.setName(flowName);

		// arguments
		if (args != null && args.size() > 0) {
			Map<String, Object> values = new HashMap<String, Object>();
			for (String key : args.keySet()) {
				String value = args.get(key);
				values.put(key, value);
			}
			efd.setValues(values);
		}

		realizedFlow.setFlowDescriptor(efd);
		return realizedFlow;
	}
}
