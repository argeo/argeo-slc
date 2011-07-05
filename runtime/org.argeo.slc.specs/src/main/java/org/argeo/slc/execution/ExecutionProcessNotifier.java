package org.argeo.slc.execution;

import java.util.List;

/**
 * Implementations of this interface can be notified of events related to
 * process execution.
 */
public interface ExecutionProcessNotifier {
	/**
	 * Notify a status change, see {@link ExecutionProcess} for the list of
	 * vaailable statuses.
	 */
	public void updateStatus(ExecutionProcess process, String oldStatus,
			String newStatus);

	/** Notifiy of new execution steps. */
	public void addSteps(ExecutionProcess process, List<ExecutionStep> steps);

}
