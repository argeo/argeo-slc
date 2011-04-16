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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.execution.ExecutionModulesManager;
import org.argeo.slc.process.RealizedFlow;
import org.argeo.slc.process.SlcExecution;

/** Thread of the SLC Process, starting the sub executions. */
public class ProcessThread extends Thread {
	private final static Log log = LogFactory.getLog(ProcessThread.class);

	private final ExecutionModulesManager executionModulesManager;
	private final SlcExecution slcProcess;
	private final ProcessThreadGroup processThreadGroup;
	private final List<RealizedFlow> flowsToProcess = new ArrayList<RealizedFlow>();

	private Boolean hadAnError = false;

	public ProcessThread(ExecutionModulesManager executionModulesManager,
			SlcExecution slcExecution) {
		super(executionModulesManager.getProcessesThreadGroup(),
				"SLC Process #" + slcExecution.getUuid());
		this.executionModulesManager = executionModulesManager;
		this.slcProcess = slcExecution;
		processThreadGroup = new ProcessThreadGroup(executionModulesManager,
				this);
	}

	public void run() {
		log.info("\n##\n## SLC Process " + slcProcess + " STARTED\n##");

		slcProcess.setStatus(SlcExecution.STATUS_RUNNING);
		executionModulesManager.dispatchUpdateStatus(slcProcess,
				SlcExecution.STATUS_SCHEDULED, SlcExecution.STATUS_RUNNING);

		flowsToProcess.addAll(slcProcess.getRealizedFlows());

		while (flowsToProcess.size() > 0) {
			RealizedFlow flow = flowsToProcess.remove(0);
			ExecutionThread thread = new ExecutionThread(this, flow);
			thread.start();

			try {
				thread.join();
			} catch (InterruptedException e) {
				log.error("Flow " + flow + " was interrupted", e);
			}

			// synchronized (this) {
			// try {
			// wait();
			// } catch (InterruptedException e) {
			// // silent
			// }
			// }
		}

		// TODO: error management at flow level?
		if (hadAnError)
			slcProcess.setStatus(SlcExecution.STATUS_ERROR);
		else
			slcProcess.setStatus(SlcExecution.STATUS_FINISHED);
		executionModulesManager.dispatchUpdateStatus(slcProcess,
				SlcExecution.STATUS_RUNNING, slcProcess.getStatus());

		log.info("## SLC Process " + slcProcess + " COMPLETED");
	}

	public void notifyError() {
		hadAnError = true;
	}

	public synchronized void flowCompleted() {
		// notifyAll();
	}

	public SlcExecution getSlcProcess() {
		return slcProcess;
	}

	public ProcessThreadGroup getProcessThreadGroup() {
		return processThreadGroup;
	}

	public ExecutionModulesManager getExecutionModulesManager() {
		return executionModulesManager;
	}
}
