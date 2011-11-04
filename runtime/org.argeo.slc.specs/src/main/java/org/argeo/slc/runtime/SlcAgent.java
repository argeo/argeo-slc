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

package org.argeo.slc.runtime;

import java.util.List;

import org.argeo.slc.execution.ExecutionModuleDescriptor;
import org.argeo.slc.execution.ExecutionProcess;

/** A local agent, able to run SLC Execution locally. */
public interface SlcAgent {
	/** Agent unique identifier */
	public String getAgentUuid();

	/** Execute / take part to this process */
	public void process(ExecutionProcess process);

	/** Kills this process */
	public void kill(ExecutionProcess process);

	public ExecutionModuleDescriptor getExecutionModuleDescriptor(
			String moduleName, String version);

	public List<ExecutionModuleDescriptor> listExecutionModuleDescriptors();

	/** @return true if still alive. */
	public boolean ping();
}
