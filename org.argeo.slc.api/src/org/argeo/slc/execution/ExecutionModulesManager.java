package org.argeo.slc.execution;

import java.util.List;

import org.argeo.slc.deploy.ModulesManager;

/** Provides access to the execution modules */
public interface ExecutionModulesManager extends ModulesManager {
	/** Used to filter event notified to an execution notifier. */
	public static String SLC_PROCESS_ID = "slc.process.id";

	/** Unique launch module */
	public static String UNIQUE_LAUNCH_MODULE_PROPERTY = "slc.launch.module";

	/** Unique launch flow */
	public static String UNIQUE_LAUNCH_FLOW_PROPERTY = "slc.launch.flow";

	/** @return a full fledged module descriptor. */
	public ExecutionModuleDescriptor getExecutionModuleDescriptor(
			String moduleName, String version);

	/**
	 * @return a list of minimal execution module descriptors (only the module
	 *         meta data, not the flows)
	 */
	public List<ExecutionModuleDescriptor> listExecutionModules();

	/** Synchronously finds and executes an {@link ExecutionFlow}. */
	public void execute(RealizedFlow realizedFlow);

	// /** Notify of a status update status of the {@link ExecutionProcess} */
//	 public void dispatchUpdateStatus(ExecutionProcess process,
//	 String oldStatus, String newStatus);
	//
	// /** Notify that a step was added in an {@link ExecutionProcess} */
	// public void dispatchAddSteps(ExecutionProcess process,
	// List<ExecutionStep> steps);
	//
	// /**
	// * Register a notifier which will be notified based on the provided
	// * properties.
	// */
	// public void registerProcessNotifier(ExecutionProcessNotifier notifier,
	// Map<String, String> properties);
	//
	// /** Unregisters a notifier */
	// public void unregisterProcessNotifier(ExecutionProcessNotifier notifier,
	// Map<String, String> properties);
}
