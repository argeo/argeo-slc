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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.execution.ExecutionModulesManager;
import org.argeo.slc.execution.ExecutionProcess;
import org.argeo.slc.execution.ExecutionStep;
import org.argeo.slc.process.RealizedFlow;
import org.argeo.slc.process.SlcExecution;

/** Thread of the SLC Process, starting the sub executions. */
@SuppressWarnings("deprecation")
public class ProcessThread extends Thread {
	private final static Log log = LogFactory.getLog(ProcessThread.class);

	private final ExecutionModulesManager executionModulesManager;
	private final ExecutionProcess process;
	private final ProcessThreadGroup processThreadGroup;

	private Set<ExecutionThread> executionThreads = Collections
			.synchronizedSet(new HashSet<ExecutionThread>());

	private Boolean hadAnError = false;
	private Boolean killed = false;

	public ProcessThread(ThreadGroup processesThreadGroup,
			ExecutionModulesManager executionModulesManager,
			ExecutionProcess process) {
		super(processesThreadGroup, "SLC Process #" + process.getUuid());
		this.executionModulesManager = executionModulesManager;
		this.process = process;
		processThreadGroup = new ProcessThreadGroup(executionModulesManager,
				this);
	}

	public final void run() {
		log.info("\n##\n## SLC Process #" + process.getUuid()
				+ " STARTED\n##\n");

		// Start logging
		new LoggingThread().start();

		String oldStatus = process.getStatus();
		process.setStatus(ExecutionProcess.RUNNING);
		executionModulesManager.dispatchUpdateStatus(process, oldStatus,
				ExecutionProcess.RUNNING);

		try {
			process();
		} catch (InterruptedException e) {
			die();
			return;
		}

		// waits for all execution threads to complete (in case they were
		// started asynchronously)
		for (ExecutionThread executionThread : executionThreads) {
			if (executionThread.isAlive()) {
				try {
					executionThread.join();
				} catch (InterruptedException e) {
					die();
					return;
				}
			}
		}

		computeFinalStatus();
	}

	/** Make sure this is called BEFORE all the threads are interrupted. */
	private void computeFinalStatus() {
		String oldStatus = process.getStatus();
		// TODO: error management at flow level?
		if (killed)
			process.setStatus(ExecutionProcess.KILLED);
		else if (hadAnError)
			process.setStatus(ExecutionProcess.ERROR);
		else
			process.setStatus(ExecutionProcess.COMPLETED);
		executionModulesManager.dispatchUpdateStatus(process, oldStatus,
				process.getStatus());
		log.info("\n## SLC Process #" + process.getUuid() + " "
				+ process.getStatus() + "\n");
	}

	/** Called when being killed */
	private synchronized void die() {
		killed = true;
		computeFinalStatus();
		for (ExecutionThread executionThread : executionThreads) {
			try {
				executionThread.interrupt();
			} catch (Exception e) {
				log.error("Cannot interrupt " + executionThread);
			}
		}
		processThreadGroup.interrupt();
	}

	/**
	 * Implementation specific execution. To be overridden in order to deal with
	 * custom process types. Default expects an {@link SlcExecution}.
	 */
	protected void process() throws InterruptedException {
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
	protected final void execute(RealizedFlow realizedFlow, Boolean synchronous)
			throws InterruptedException {
		if (killed)
			return;

		ExecutionThread thread = new ExecutionThread(this, realizedFlow);
		executionThreads.add(thread);
		thread.start();

		if (synchronous)
			thread.join();

		return;
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

	private class LoggingThread extends Thread {

		public LoggingThread() {
			super("SLC Process Logger #" + process.getUuid());
		}

		public void run() {
			boolean run = true;
			while (run) {
				List<ExecutionStep> newSteps = new ArrayList<ExecutionStep>();
				processThreadGroup.getSteps().drainTo(newSteps);
				if (newSteps.size() > 0) {
					// System.out.println(steps.size() + " steps");
					process.addSteps(newSteps);
				}

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					break;
				}

				if (!ProcessThread.this.isAlive()
						&& processThreadGroup.getSteps().size() == 0)
					run = false;
			}
		}

	}
}
