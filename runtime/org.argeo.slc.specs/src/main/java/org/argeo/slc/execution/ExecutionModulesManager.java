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

package org.argeo.slc.execution;

import java.util.List;

import org.argeo.slc.deploy.ModulesManager;
import org.argeo.slc.process.RealizedFlow;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.process.SlcExecutionStep;

/** Provides access to the execution modules */
public interface ExecutionModulesManager extends ModulesManager {
	/** @return a full fledged module descriptor. */
	public ExecutionModuleDescriptor getExecutionModuleDescriptor(
			String moduleName, String version);

	/**
	 * @return a list of minimal execution module descriptors (only the module
	 *         meta data, not the flows)
	 */
	public List<ExecutionModuleDescriptor> listExecutionModules();

	/** Asynchronously prepares and executes an {@link SlcExecution} */
	public void process(SlcExecution slcExecution);

	/** The thread group to which all process threads will belong. */
	public ThreadGroup getProcessesThreadGroup();

	/** Synchronously finds and executes an {@link ExecutionFlow}. */
	public void execute(RealizedFlow realizedFlow);

	/** Notify of a status update status of the {@link SlcExecution} */
	public void dispatchUpdateStatus(SlcExecution slcExecution,
			String oldStatus, String newStatus);

	/** Notify that a step was added in an {@link SlcExecution} */
	public void dispatchAddStep(SlcExecution slcExecution, SlcExecutionStep step);
}
