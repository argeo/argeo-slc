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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.execution.ExecutionModulesManager;
import org.argeo.slc.execution.ExecutionProcess;
import org.argeo.slc.process.RealizedFlow;
import org.argeo.slc.process.SlcExecution;

/** Thread of the SLC Process, starting the sub executions. */
public class ProcessThread extends Thread {
	private final static Log log = LogFactory.getLog(ProcessThread.class);

	private final ExecutionModulesManager executionModulesManager;
	private final ExecutionProcess process;
	private final ProcessThreadGroup processThreadGroup;

	private Set<ExecutionThread> executionThreads = new HashSet<ExecutionThread>();

	private Boolean hadAnError = false;

	public ProcessThread(ExecutionModulesManager executionModulesManager,
			ExecutionProcess process) {
		super(executionModulesManager.getProcessesThreadGroup(),
				"SLC Process #" + process.getUuid());
		this.executionModulesManager = executionModulesManager;
		this.process = process;
		processThreadGroup = new ProcessThreadGroup(executionModulesManager,
				this);
	}

	public void run() {
		log.info("\n##\n## SLC Process #" + process.getUuid()
				+ " STARTED\n##\n");

		process.setStatus(SlcExecution.RUNNING);
		executionModulesManager.dispatchUpdateStatus(process,
				SlcExecution.SCHEDULED, SlcExecution.RUNNING);

		process();

		// waits for all execution threads to complete (in case they were
		// started asynchronously)
		for (ExecutionThread executionThread : executionThreads) {
			if (executionThread.isAlive()) {
				try {
					executionThread.join();
				} catch (InterruptedException e) {
					log.error("Execution thread " + executionThread
							+ " was interrupted");
				}
			}
		}

		// TODO: error management at flow level?
		if (hadAnError)
			process.setStatus(SlcExecution.ERROR);
		else
			process.setStatus(SlcExecution.COMPLETED);
		executionModulesManager.dispatchUpdateStatus(process,
				SlcExecution.RUNNING, process.getStatus());

		log.info("\n## SLC Process #" + process.getUuid() + " COMPLETED\n");
	}

	/**
	 * Implementation specific execution. To be overridden in order to deal with
	 * custom process types. Default expects an {@link SlcExecution}.
	 */
	protected void process() {
		if (!(process instanceof SlcExecution))
			throw new SlcException("Unsupported process type "
					+ process.getClass());
		SlcExecution slcExecution = (SlcExecution) process;
		List<RealizedFlow> flowsToProcess = new ArrayList<RealizedFlow>();
		flowsToProcess.addAll(slcExecution.getRealizedFlows());

		while (flowsToProcess.size() > 0) {
			RealizedFlow realizedFlow = flowsToProcess.remove(0);
			execute(realizedFlow, true);
		}
	}

	/** @return the (distinct) thread used for this execution */
	protected Thread execute(RealizedFlow realizedFlow, Boolean synchronous) {
		ExecutionThread thread = new ExecutionThread(this, realizedFlow);
		executionThreads.add(thread);
		thread.start();

		if (synchronous) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				log.error("Flow " + realizedFlow + " was interrupted", e);
			}
		}
		return thread;

		// synchronized (this) {
		// try {
		// wait();
		// } catch (InterruptedException e) {
		// // silent
		// }
		// }
	}

	public void notifyError() {
		hadAnError = true;
	}

	public synchronized void flowCompleted() {
		// notifyAll();
	}

	public ExecutionProcess getProcess() {
		return process;
	}

	public ProcessThreadGroup getProcessThreadGroup() {
		return processThreadGroup;
	}

	public ExecutionModulesManager getExecutionModulesManager() {
		return executionModulesManager;
	}
}
