package org.argeo.slc.core.execution;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.process.SlcExecutionNotifier;
import org.argeo.slc.process.SlcExecutionStep;

public class ProcessThreadGroup extends ThreadGroup {
	private final ProcessThread processThread;

	public ProcessThreadGroup(ProcessThread processThread) {
		super("SLC Process #" + processThread.getSlcProcess().getUuid()
				+ " thread group");
		this.processThread = processThread;
	}

	public SlcExecution getSlcProcess() {
		return processThread.getSlcProcess();
	}

	public void dispatchAddStep(SlcExecutionStep step) {
		processThread.getSlcProcess().getSteps().add(step);
		List<SlcExecutionStep> steps = new ArrayList<SlcExecutionStep>();
		steps.add(step);
		for (Iterator<SlcExecutionNotifier> it = processThread
				.getExecutionModulesManager().getSlcExecutionNotifiers()
				.iterator(); it.hasNext();) {
			it.next().addSteps(processThread.getSlcProcess(), steps);
		}
	}

}
