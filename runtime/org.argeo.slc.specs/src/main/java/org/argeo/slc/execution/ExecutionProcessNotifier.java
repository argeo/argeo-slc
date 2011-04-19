package org.argeo.slc.execution;

import java.util.List;

import org.argeo.slc.process.SlcExecutionStep;

public interface ExecutionProcessNotifier {
	public void addSteps(ExecutionProcess process,
			List<SlcExecutionStep> additionalSteps);

	public void updateStatus(ExecutionProcess process, String oldStatus,
			String newStatus);

}
