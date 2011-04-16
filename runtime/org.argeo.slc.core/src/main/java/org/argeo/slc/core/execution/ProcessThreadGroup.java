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

package org.argeo.slc.core.execution;

import org.argeo.slc.execution.ExecutionModulesManager;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.process.SlcExecutionStep;

/** The thread group attached to a given {@link SlcExecution}. */
public class ProcessThreadGroup extends ThreadGroup {
	private final ExecutionModulesManager executionModulesManager;
	private final ProcessThread processThread;

	public ProcessThreadGroup(ExecutionModulesManager executionModulesManager,
			ProcessThread processThread) {
		super("SLC Process #" + processThread.getSlcProcess().getUuid()
				+ " thread group");
		this.executionModulesManager = executionModulesManager;
		this.processThread = processThread;
	}

	public SlcExecution getSlcProcess() {
		return processThread.getSlcProcess();
	}

	public void dispatchAddStep(SlcExecutionStep step) {
		SlcExecution slcProcess = processThread.getSlcProcess();
		slcProcess.getSteps().add(step);
		executionModulesManager.dispatchAddStep(slcProcess, step);
	}

}
