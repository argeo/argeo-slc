package org.argeo.slc.runtime;

import java.util.Map;

import org.argeo.slc.execution.ExecutionContext;
import org.argeo.slc.execution.ExecutionFlow;
import org.argeo.slc.execution.ExecutionFlowDescriptorConverter;
import org.argeo.slc.execution.ExecutionModulesManager;
import org.argeo.slc.execution.RealizedFlow;

/** Provides the base feature of an execution module manager. */
public abstract class AbstractExecutionModulesManager implements
		ExecutionModulesManager {

	// private List<FilteredNotifier> filteredNotifiers = Collections
	// .synchronizedList(new ArrayList<FilteredNotifier>());

	protected abstract ExecutionFlow findExecutionFlow(String moduleName,
			String moduleVersion, String flowName);

	protected abstract ExecutionContext findExecutionContext(String moduleName,
			String moduleVersion);

	protected abstract ExecutionFlowDescriptorConverter getExecutionFlowDescriptorConverter(
			String moduleName, String moduleVersion);

	public void execute(RealizedFlow realizedFlow) {
		String moduleName = realizedFlow.getModuleName();
		String moduleVersion = realizedFlow.getModuleVersion();

		Map<? extends String, ? extends Object> variablesToAdd = getExecutionFlowDescriptorConverter(
				moduleName, moduleVersion).convertValues(
				realizedFlow.getFlowDescriptor());
		ExecutionContext executionContext = findExecutionContext(moduleName,
				moduleVersion);
		for (String key : variablesToAdd.keySet())
			executionContext.setVariable(key, variablesToAdd.get(key));

		ExecutionFlow flow = findExecutionFlow(moduleName, moduleVersion,
				realizedFlow.getFlowDescriptor().getName());

		//
		// Actually runs the flow, IN THIS THREAD
		//
		executionContext.beforeFlow(flow);
		try {
			flow.run();
		} finally {
			executionContext.afterFlow(flow);
		}
		//
		//
		//
	}

	// public void dispatchUpdateStatus(ExecutionProcess process,
	// String oldStatus, String newStatus) {
	// // filtered notifiers
	// for (Iterator<FilteredNotifier> it = filteredNotifiers.iterator(); it
	// .hasNext();) {
	// FilteredNotifier filteredNotifier = it.next();
	// if (filteredNotifier.receiveFrom(process))
	// filteredNotifier.getNotifier().updateStatus(process, oldStatus,
	// newStatus);
	// }
	//
	// }

	// public void dispatchAddSteps(ExecutionProcess process,
	// List<ExecutionStep> steps) {
	// process.addSteps(steps);
	// for (Iterator<FilteredNotifier> it = filteredNotifiers.iterator(); it
	// .hasNext();) {
	// FilteredNotifier filteredNotifier = it.next();
	// if (filteredNotifier.receiveFrom(process))
	// filteredNotifier.getNotifier().addSteps(process, steps);
	// }
	// }

	// public void registerProcessNotifier(ExecutionProcessNotifier notifier,
	// Map<String, String> properties) {
	// filteredNotifiers.add(new FilteredNotifier(notifier, properties));
	// }
	//
	// public void unregisterProcessNotifier(ExecutionProcessNotifier notifier,
	// Map<String, String> properties) {
	// filteredNotifiers.remove(notifier);
	// }

	// protected class FilteredNotifier {
	// private final ExecutionProcessNotifier notifier;
	// private final String processId;
	//
	// public FilteredNotifier(ExecutionProcessNotifier notifier,
	// Map<String, String> properties) {
	// super();
	// this.notifier = notifier;
	// if (properties == null)
	// properties = new HashMap<String, String>();
	// if (properties.containsKey(SLC_PROCESS_ID))
	// processId = properties.get(SLC_PROCESS_ID);
	// else
	// processId = null;
	// }
	//
	// /**
	// * Whether event from this process should be received by this listener.
	// */
	// public Boolean receiveFrom(ExecutionProcess process) {
	// if (processId != null)
	// if (process.getUuid().equals(processId))
	// return true;
	// else
	// return false;
	// return true;
	// }
	//
	// @Override
	// public int hashCode() {
	// return notifier.hashCode();
	// }
	//
	// @Override
	// public boolean equals(Object obj) {
	// if (obj instanceof FilteredNotifier) {
	// FilteredNotifier fn = (FilteredNotifier) obj;
	// return notifier.equals(fn.notifier);
	// } else if (obj instanceof ExecutionProcessNotifier) {
	// ExecutionProcessNotifier epn = (ExecutionProcessNotifier) obj;
	// return notifier.equals(epn);
	// } else
	// return false;
	// }
	//
	// public ExecutionProcessNotifier getNotifier() {
	// return notifier;
	// }
	//
	// }
}
