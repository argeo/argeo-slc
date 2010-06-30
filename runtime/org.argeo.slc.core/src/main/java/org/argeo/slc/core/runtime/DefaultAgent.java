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

package org.argeo.slc.core.runtime;

import java.util.List;

import org.argeo.slc.execution.ExecutionModuleDescriptor;
import org.argeo.slc.execution.ExecutionModulesManager;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.runtime.SlcAgent;

public class DefaultAgent implements SlcAgent {
	// private final static Log log = LogFactory.getLog(AbstractAgent.class);

	private ExecutionModulesManager modulesManager;

	public void runSlcExecution(final SlcExecution slcExecution) {
		modulesManager.process(slcExecution);
	}

	public ExecutionModuleDescriptor getExecutionModuleDescriptor(
			String moduleName, String version) {
		return modulesManager.getExecutionModuleDescriptor(moduleName,
				version);
	}

	public List<ExecutionModuleDescriptor> listExecutionModuleDescriptors() {
		return modulesManager.listExecutionModules();
	}

	public boolean ping() {
		return true;
	}

	public void setModulesManager(ExecutionModulesManager modulesManager) {
		this.modulesManager = modulesManager;
	}

	public ExecutionModulesManager getModulesManager() {
		return modulesManager;
	}

}
