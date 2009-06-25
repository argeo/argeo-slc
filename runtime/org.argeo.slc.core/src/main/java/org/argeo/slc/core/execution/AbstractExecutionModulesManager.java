package org.argeo.slc.core.execution;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.execution.ExecutionFlow;
import org.argeo.slc.execution.ExecutionFlowDescriptor;
import org.argeo.slc.execution.ExecutionModuleDescriptor;
import org.argeo.slc.execution.ExecutionModulesManager;
import org.argeo.slc.execution.ExecutionSpec;
import org.argeo.slc.execution.ExecutionSpecAttribute;
import org.argeo.slc.process.RealizedFlow;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.process.SlcExecutionNotifier;
import org.argeo.slc.process.SlcExecutionStep;
import org.springframework.aop.scope.ScopedObject;
import org.springframework.util.Assert;

public abstract class AbstractExecutionModulesManager implements
		ExecutionModulesManager {
	private final static Log log = LogFactory
			.getLog(AbstractExecutionModulesManager.class);

	private List<SlcExecutionNotifier> slcExecutionNotifiers = new ArrayList<SlcExecutionNotifier>();
	private ThreadGroup processesThreadGroup = new ThreadGroup("Processes");

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

	protected static ExecutionModuleDescriptor createDescriptor(
			String moduleName, String moduleVersion,
			Map<String, ExecutionFlow> executionFlows) {
		// TODO: put this in a separate configurable object
		ExecutionModuleDescriptor md = new ExecutionModuleDescriptor();
		md.setName(moduleName);
		md.setVersion(moduleVersion);

		for (String name : executionFlows.keySet()) {
			ExecutionFlow executionFlow = executionFlows.get(name);

			Assert.notNull(executionFlow.getName());
			Assert.state(name.equals(executionFlow.getName()));

			ExecutionSpec executionSpec = executionFlow.getExecutionSpec();
			Assert.notNull(executionSpec);
			Assert.notNull(executionSpec.getName());

			Map<String, Object> values = new TreeMap<String, Object>();
			for (String key : executionSpec.getAttributes().keySet()) {
				ExecutionSpecAttribute attribute = executionSpec
						.getAttributes().get(key);

				if (executionFlow.isSetAsParameter(key)) {
					Object value = executionFlow.getParameter(key);
					if (attribute instanceof PrimitiveSpecAttribute) {
						PrimitiveValue primitiveValue = new PrimitiveValue();
						primitiveValue
								.setType(((PrimitiveSpecAttribute) attribute)
										.getType());
						primitiveValue.setValue(value);
						values.put(key, primitiveValue);
					} else if (attribute instanceof RefSpecAttribute) {
						RefValue refValue = new RefValue();
						if (value instanceof ScopedObject) {
							refValue.setLabel("RUNTIME "
									+ value.getClass().getName());
						} else {
							refValue.setLabel("STATIC "
									+ value.getClass().getName());
						}
						values.put(key, refValue);
					} else {
						throw new SlcException("Unkown spec attribute type "
								+ attribute.getClass());
					}
				}

			}

			ExecutionFlowDescriptor efd = new ExecutionFlowDescriptor(name,
					values, executionSpec);
			if (executionFlow.getPath() != null)
				efd.setPath(executionFlow.getPath());

			// Add execution spec if necessary
			if (!md.getExecutionSpecs().contains(executionSpec))
				md.getExecutionSpecs().add(executionSpec);

			// Add execution flow
			md.getExecutionFlows().add(efd);
		}

		return md;
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
		private final RealizedFlow realizedFlow;
		private final ProcessThread processThread;

		public ExecutionThread(ProcessThread processThread,
				RealizedFlow realizedFlow) {
			super(processThread.getProcessThreadGroup(), "Flow "
					+ realizedFlow.getFlowDescriptor().getName());
			this.realizedFlow = realizedFlow;
			this.processThread = processThread;
		}

		public void run() {
			ExecutionFlowDescriptor executionFlowDescriptor = realizedFlow
					.getFlowDescriptor();
			String flowName = executionFlowDescriptor.getName();

			dispatchAddStep(processThread.getSlcProcess(),
					new SlcExecutionStep(SlcExecutionStep.TYPE_PHASE_START,
							"Flow " + flowName));

			try {
				execute(realizedFlow);
			} catch (Exception e) {
				// TODO: re-throw exception ?
				String msg = "Execution of flow " + flowName + " failed.";
				log.error(msg, e);
				dispatchAddStep(processThread.getSlcProcess(),
						new SlcExecutionStep(msg + " " + e.getMessage()));
			} finally {
				processThread.flowCompleted();
				dispatchAddStep(processThread.getSlcProcess(),
						new SlcExecutionStep(SlcExecutionStep.TYPE_PHASE_END,
								"Flow " + flowName));
			}
		}
	}

}
