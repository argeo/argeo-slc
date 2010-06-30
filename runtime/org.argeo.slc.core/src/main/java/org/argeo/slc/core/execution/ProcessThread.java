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
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.process.RealizedFlow;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.process.SlcExecutionNotifier;

/** Thread of the SLC Process, starting the sub executions. */
public class ProcessThread extends Thread {
	private final static Log log = LogFactory.getLog(ProcessThread.class);

	private final AbstractExecutionModulesManager executionModulesManager;
	private final SlcExecution slcProcess;
	private final ProcessThreadGroup processThreadGroup;
	private final List<RealizedFlow> flowsToProcess = new ArrayList<RealizedFlow>();

	private Boolean hadAnError = false;

	public ProcessThread(
			AbstractExecutionModulesManager executionModulesManager,
			SlcExecution slcExecution) {
		super(executionModulesManager.getProcessesThreadGroup(),
				"SLC Process #" + slcExecution.getUuid());
		this.executionModulesManager = executionModulesManager;
		this.slcProcess = slcExecution;
		processThreadGroup = new ProcessThreadGroup(this);
	}

	public void run() {
		log.info("\n##\n## Process SLC Execution " + slcProcess + "\n##\n");

		slcProcess.setStatus(SlcExecution.STATUS_RUNNING);
		dispatchUpdateStatus(slcProcess, SlcExecution.STATUS_SCHEDULED,
				SlcExecution.STATUS_RUNNING);

		flowsToProcess.addAll(slcProcess.getRealizedFlows());

		while (flowsToProcess.size() > 0) {
			RealizedFlow flow = flowsToProcess.remove(0);
			ExecutionThread thread = new ExecutionThread(this, flow);
			thread.start();

			synchronized (this) {
				try {
					wait();
				} catch (InterruptedException e) {
					// silent
				}
			}
		}

		if (hadAnError)
			slcProcess.setStatus(SlcExecution.STATUS_ERROR);
		else
			slcProcess.setStatus(SlcExecution.STATUS_FINISHED);
		dispatchUpdateStatus(slcProcess, SlcExecution.STATUS_RUNNING,
				slcProcess.getStatus());
	}

	protected void dispatchUpdateStatus(SlcExecution slcExecution,
			String oldStatus, String newStatus) {
		for (Iterator<SlcExecutionNotifier> it = executionModulesManager
				.getSlcExecutionNotifiers().iterator(); it.hasNext();) {
			it.next().updateStatus(slcExecution, oldStatus, newStatus);
		}
	}

	public void notifyError() {
		hadAnError = true;
	}

	public synchronized void flowCompleted() {
		notifyAll();
	}

	public SlcExecution getSlcProcess() {
		return slcProcess;
	}

	public ProcessThreadGroup getProcessThreadGroup() {
		return processThreadGroup;
	}

	public AbstractExecutionModulesManager getExecutionModulesManager() {
		return executionModulesManager;
	}
}
