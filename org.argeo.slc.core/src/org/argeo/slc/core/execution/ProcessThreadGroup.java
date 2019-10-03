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
package org.argeo.slc.core.execution;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.argeo.slc.execution.ExecutionProcess;
import org.argeo.slc.execution.ExecutionStep;

/** The thread group attached to a given {@link SlcExecution}. */
public class ProcessThreadGroup extends ThreadGroup {
//	private final Authentication authentication;
	private final static Integer STEPS_BUFFER_CAPACITY = 5000;

	private BlockingQueue<ExecutionStep> steps = new ArrayBlockingQueue<ExecutionStep>(
			STEPS_BUFFER_CAPACITY);

	private Boolean hadAnError = false;

	public ProcessThreadGroup(ExecutionProcess executionProcess) {
		super("SLC Process #" + executionProcess.getUuid() + " thread group");
//		this.authentication = SecurityContextHolder.getContext()
//				.getAuthentication();
	}

//	public Authentication getAuthentication() {
//		return authentication;
//	}

	public void dispatchAddStep(ExecutionStep step) {
		// ExecutionProcess slcProcess = processThread.getProcess();
		// List<ExecutionStep> steps = new ArrayList<ExecutionStep>();
		// steps.add(step);
		// TODO clarify why we don't dispatch steps, must be a reason
		// dispatchAddSteps(steps);
		// slcProcess.addSteps(steps);
		if (step.getType().equals(ExecutionStep.ERROR))
			hadAnError = true;
		this.steps.add(step);
	}

	// public void dispatchAddSteps(List<ExecutionStep> steps) {
	// ExecutionProcess slcProcess = processThread.getProcess();
	// executionModulesManager.dispatchAddSteps(slcProcess, steps);
	// }

	public BlockingQueue<ExecutionStep> getSteps() {
		return steps;
	}

	public Boolean hadAnError() {
		return hadAnError;
	}
}
