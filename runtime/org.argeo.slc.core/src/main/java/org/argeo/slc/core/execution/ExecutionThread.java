package org.argeo.slc.core.execution;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.execution.ExecutionFlowDescriptor;
import org.argeo.slc.process.RealizedFlow;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.process.SlcExecutionNotifier;
import org.argeo.slc.process.SlcExecutionStep;

/** Thread of a single execution */
public class ExecutionThread extends Thread {
	public final static String SYSPROP_EXECUTION_AUTO_UPGRADE = "slc.execution.autoupgrade";

	private final static Log log = LogFactory.getLog(ExecutionThread.class);

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
		if (getContextClassLoader() != null) {
			if (log.isTraceEnabled())
				log.debug("Context class loader set to "
						+ getContextClassLoader());
		}

		// Retrieve execution flow descriptor
		ExecutionFlowDescriptor executionFlowDescriptor = realizedFlow
				.getFlowDescriptor();
		String flowName = executionFlowDescriptor.getName();

		dispatchAddStep(processThread.getSlcProcess(), new SlcExecutionStep(
				SlcExecutionStep.TYPE_PHASE_START, "Flow " + flowName));

		try {
			String autoUpgrade = System
					.getProperty(SYSPROP_EXECUTION_AUTO_UPGRADE);
			if (autoUpgrade != null && autoUpgrade.equals("true"))
				processThread.getExecutionModulesManager().upgrade(
						realizedFlow.getModuleNameVersion());
			processThread.getExecutionModulesManager().execute(realizedFlow);
		} catch (Exception e) {
			// TODO: re-throw exception ?
			String msg = "Execution of flow " + flowName + " failed.";
			log.error(msg, e);
			dispatchAddStep(processThread.getSlcProcess(),
					new SlcExecutionStep(msg + " " + e.getMessage()));
			processThread.notifyError();
		} finally {
			processThread.flowCompleted();
			dispatchAddStep(processThread.getSlcProcess(),
					new SlcExecutionStep(SlcExecutionStep.TYPE_PHASE_END,
							"Flow " + flowName));
		}
	}

	protected void dispatchAddStep(SlcExecution slcExecution,
			SlcExecutionStep step) {
		slcExecution.getSteps().add(step);
		List<SlcExecutionStep> steps = new ArrayList<SlcExecutionStep>();
		steps.add(step);
		for (Iterator<SlcExecutionNotifier> it = processThread
				.getExecutionModulesManager().getSlcExecutionNotifiers()
				.iterator(); it.hasNext();) {
			it.next().addSteps(slcExecution, steps);
		}
	}
}
