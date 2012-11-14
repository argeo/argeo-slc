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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.execution.ExecutionFlowDescriptor;
import org.argeo.slc.execution.ExecutionStep;
import org.argeo.slc.execution.RealizedFlow;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;

/** Thread of a single execution */
public class ExecutionThread extends Thread {
	public final static String SYSPROP_EXECUTION_AUTO_UPGRADE = "slc.execution.autoupgrade";

	private final static Log log = LogFactory.getLog(ExecutionThread.class);

	private final RealizedFlow realizedFlow;
	private final ProcessThread processThread;

	private List<Runnable> destructionCallbacks = new ArrayList<Runnable>();

	public ExecutionThread(ProcessThread processThread,
			RealizedFlow realizedFlow) {
		super(processThread.getProcessThreadGroup(), "Flow "
				+ realizedFlow.getFlowDescriptor().getName());
		this.realizedFlow = realizedFlow;
		this.processThread = processThread;
	}

	public void run() {
		// authenticate thread
		Authentication authentication = getProcessThreadGroup()
				.getAuthentication();
		if (authentication == null)
			throw new SlcException("Can only execute authenticated threads");
		SecurityContextHolder.getContext().setAuthentication(authentication);

		// Retrieve execution flow descriptor
		ExecutionFlowDescriptor executionFlowDescriptor = realizedFlow
				.getFlowDescriptor();
		String flowName = executionFlowDescriptor.getName();

		dispatchAddStep(new ExecutionStep(realizedFlow.getModuleName(),
				ExecutionStep.PHASE_START, "Flow " + flowName));

		try {
			String autoUpgrade = System
					.getProperty(SYSPROP_EXECUTION_AUTO_UPGRADE);
			if (autoUpgrade != null && autoUpgrade.equals("true"))
				processThread.getExecutionModulesManager().upgrade(
						realizedFlow.getModuleNameVersion());

			// START FLOW
			processThread.getExecutionModulesManager().execute(realizedFlow);
			// END FLOW
		} catch (Exception e) {
			// TODO: re-throw exception ?
			String msg = "Execution of flow " + flowName + " failed.";
			log.error(msg, e);
			dispatchAddStep(new ExecutionStep(realizedFlow.getModuleName(),
					ExecutionStep.ERROR, msg + " " + e.getMessage()));
			processThread.notifyError();
		} finally {
			processThread.flowCompleted();
			dispatchAddStep(new ExecutionStep(realizedFlow.getModuleName(),
					ExecutionStep.PHASE_END, "Flow " + flowName));
			processDestructionCallbacks();
		}
	}

	private void dispatchAddStep(ExecutionStep step) {
		processThread.getProcessThreadGroup().dispatchAddStep(step);
	}

	private synchronized void processDestructionCallbacks() {
		for (int i = destructionCallbacks.size() - 1; i >= 0; i--) {
			try {
				destructionCallbacks.get(i).run();
			} catch (Exception e) {
				log.warn("Could not process destruction callback " + i
						+ " in thread " + getName(), e);
			}
		}
	}

	/**
	 * Gather object destruction callback to be called in reverse order at the
	 * end of the thread
	 */
	synchronized void registerDestructionCallback(String name, Runnable callback) {
		destructionCallbacks.add(callback);
	}

	protected ProcessThreadGroup getProcessThreadGroup() {
		return processThread.getProcessThreadGroup();
	}

	public RealizedFlow getRealizedFlow() {
		return realizedFlow;
	}

}
