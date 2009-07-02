package org.argeo.slc.core.execution.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.core.execution.AbstractExecutionModulesManager;
import org.argeo.slc.process.RealizedFlow;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.process.SlcExecutionNotifier;

/** Thread of the SLC Process, starting the sub executions. */
public class ProcessThread extends Thread {
	private final static Log log = LogFactory.getLog(ProcessThread.class);

	private final AbstractExecutionModulesManager executionModulesManager;
	private final SlcExecution slcProcess;
	private final ThreadGroup processThreadGroup;
	private final List<RealizedFlow> flowsToProcess = new ArrayList<RealizedFlow>();

	public ProcessThread(
			AbstractExecutionModulesManager executionModulesManager,
			SlcExecution slcExecution) {
		super(executionModulesManager.getProcessesThreadGroup(),
				"SLC Process #" + slcExecution.getUuid());
		this.executionModulesManager = executionModulesManager;
		this.slcProcess = slcExecution;
		processThreadGroup = new ThreadGroup("SLC Process #"
				+ slcExecution.getUuid() + " thread group");
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

		slcProcess.setStatus(SlcExecution.STATUS_FINISHED);
		dispatchUpdateStatus(slcProcess, SlcExecution.STATUS_RUNNING,
				SlcExecution.STATUS_FINISHED);
	}

	protected void dispatchUpdateStatus(SlcExecution slcExecution,
			String oldStatus, String newStatus) {
		for (Iterator<SlcExecutionNotifier> it = executionModulesManager
				.getSlcExecutionNotifiers().iterator(); it.hasNext();) {
			it.next().updateStatus(slcExecution, oldStatus, newStatus);
		}
	}

	public synchronized void flowCompleted() {
		notifyAll();
	}

	public SlcExecution getSlcProcess() {
		return slcProcess;
	}

	public ThreadGroup getProcessThreadGroup() {
		return processThreadGroup;
	}

	public AbstractExecutionModulesManager getExecutionModulesManager() {
		return executionModulesManager;
	}
}
