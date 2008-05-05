package org.argeo.slc.core.process;

import java.util.List;

public interface SlcExecutionNotifier {
	public void newExecution(SlcExecution slcExecution);

	public void addSteps(SlcExecution slcExecution,
			List<SlcExecutionStep> additionalSteps);

	public void updateExecution(SlcExecution slcExecution);

	public void updateStatus(SlcExecution slcExecution, String oldStatus,
			String newStatus);
}
