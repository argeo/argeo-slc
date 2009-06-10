package org.argeo.slc.core.execution;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.execution.ExecutionFlowDescriptor;
import org.argeo.slc.execution.ExecutionModule;
import org.argeo.slc.execution.ExecutionModuleDescriptor;
import org.argeo.slc.execution.ExecutionModulesManager;
import org.argeo.slc.process.RealizedFlow;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.process.SlcExecutionNotifier;
import org.argeo.slc.process.SlcExecutionStep;

public class DefaultModulesManager implements ExecutionModulesManager {
	private final static Log log = LogFactory
			.getLog(DefaultModulesManager.class);

	private List<ExecutionModule> executionModules = new ArrayList<ExecutionModule>();
	private List<SlcExecutionNotifier> slcExecutionNotifiers = new ArrayList<SlcExecutionNotifier>();
	private ThreadGroup processesThreadGroup = new ThreadGroup("Processes");

	protected ExecutionModule getExecutionModule(String moduleName,
			String version) {
		for (ExecutionModule moduleT : executionModules) {
			if (moduleT.getName().equals(moduleName)) {
				if (moduleT.getVersion().equals(version)) {
					return moduleT;
				}
			}
		}
		return null;
	}

	public ExecutionModuleDescriptor getExecutionModuleDescriptor(
			String moduleName, String version) {
		ExecutionModule module = getExecutionModule(moduleName, version);

		if (module == null)
			throw new SlcException("Module " + moduleName + " (" + version
					+ ") not found");

		return module.getDescriptor();
	}

	public List<ExecutionModule> listExecutionModules() {
		return executionModules;
	}

	public void setExecutionModules(List<ExecutionModule> executionModules) {
		this.executionModules = executionModules;
	}

	public void process(SlcExecution slcExecution) {
		new ProcessThread(processesThreadGroup, slcExecution).start();
	}

	protected void dispatchUpdateStatus(SlcExecution slcExecution,
			String oldStatus, String newStatus) {
		for (Iterator<SlcExecutionNotifier> it = slcExecutionNotifiers
				.iterator(); it.hasNext();) {
			it.next().updateStatus(slcExecution, oldStatus, newStatus);
		}
	}

	protected synchronized void dispatchAddStep(SlcExecution slcExecution,
			SlcExecutionStep step) {
		slcExecution.getSteps().add(step);
		List<SlcExecutionStep> steps = new ArrayList<SlcExecutionStep>();
		steps.add(step);
		for (Iterator<SlcExecutionNotifier> it = slcExecutionNotifiers
				.iterator(); it.hasNext();) {
			it.next().addSteps(slcExecution, steps);
		}
	}

	public void setSlcExecutionNotifiers(
			List<SlcExecutionNotifier> slcExecutionNotifiers) {
		this.slcExecutionNotifiers = slcExecutionNotifiers;
	}

	/** Thread of the SLC Process, starting the sub executions. */
	private class ProcessThread extends Thread {
		private final SlcExecution slcProcess;
		private final ThreadGroup processThreadGroup;
		private final List<RealizedFlow> flowsToProcess = new ArrayList<RealizedFlow>();

		public ProcessThread(ThreadGroup processesThreadGroup,
				SlcExecution slcExecution) {
			super(processesThreadGroup, "SLC Process #"
					+ slcExecution.getUuid());
			this.slcProcess = slcExecution;
			processThreadGroup = new ThreadGroup("SLC Process #"
					+ slcExecution.getUuid() + " thread group");
		}

		public void run() {
			log.info("\n##\n## Process SLC Execution " + slcProcess + "\n##\n");

			// FIXME: hack to let the SlcExecution be registered on server
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				// silent
			}

			slcProcess.setStatus(SlcExecution.STATUS_RUNNING);
			dispatchUpdateStatus(slcProcess, SlcExecution.STATUS_SCHEDULED,
					SlcExecution.STATUS_RUNNING);

			flowsToProcess.addAll(slcProcess.getRealizedFlows());

			while (flowsToProcess.size() > 0) {
				RealizedFlow flow = flowsToProcess.remove(0);
				ExecutionModule module = getExecutionModule(flow
						.getModuleName(), flow.getModuleVersion());
				if (module != null) {
					ExecutionThread thread = new ExecutionThread(this, flow
							.getFlowDescriptor(), module);
					thread.start();
				} else {
					throw new SlcException("ExecutionModule "
							+ flow.getModuleName() + ", version "
							+ flow.getModuleVersion() + " not found.");
				}

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

		public synchronized void flowCompleted() {
			notifyAll();
		}

		public SlcExecution getSlcProcess() {
			return slcProcess;
		}

		public ThreadGroup getProcessThreadGroup() {
			return processThreadGroup;
		}
	}

	/** Thread of a single execution */
	private class ExecutionThread extends Thread {
		private final ExecutionFlowDescriptor executionFlowDescriptor;
		private final ExecutionModule executionModule;
		private final ProcessThread processThread;

		public ExecutionThread(ProcessThread processThread,
				ExecutionFlowDescriptor executionFlowDescriptor,
				ExecutionModule executionModule) {
			super(processThread.getProcessThreadGroup(), "Flow "
					+ executionFlowDescriptor.getName());
			this.executionFlowDescriptor = executionFlowDescriptor;
			this.executionModule = executionModule;
			this.processThread = processThread;
		}

		public void run() {
			dispatchAddStep(processThread.getSlcProcess(),
					new SlcExecutionStep(SlcExecutionStep.TYPE_PHASE_START,
							"Flow " + executionFlowDescriptor.getName()));

			try {
				executionModule.execute(executionFlowDescriptor);
			} catch (Exception e) {
				// TODO: re-throw exception ?
				String msg = "Execution of flow "
						+ executionFlowDescriptor.getName() + " failed.";
				log.error(msg, e);
				dispatchAddStep(processThread.getSlcProcess(),
						new SlcExecutionStep(msg + " " + e.getMessage()));
			} finally {
				processThread.flowCompleted();
				dispatchAddStep(processThread.getSlcProcess(),
						new SlcExecutionStep(SlcExecutionStep.TYPE_PHASE_END,
								"Flow " + executionFlowDescriptor.getName()));
			}
		}
	}
}
