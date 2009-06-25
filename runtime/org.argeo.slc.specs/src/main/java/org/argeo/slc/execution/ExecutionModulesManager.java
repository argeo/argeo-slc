package org.argeo.slc.execution;

import java.util.List;

import org.argeo.slc.process.RealizedFlow;
import org.argeo.slc.process.SlcExecution;

/** Provides access to the execution modules */
public interface ExecutionModulesManager {
	/** @return a full fledged module descriptor. */
	public ExecutionModuleDescriptor getExecutionModuleDescriptor(
			String moduleName, String version);

	/**
	 * @return a list of minimal execution module descriptors (only the module
	 *         meta data, not the flows)
	 */
	public List<ExecutionModuleDescriptor> listExecutionModules();

	/** Asynchronously prepare and executes an {@link SlcExecution} */
	public void process(SlcExecution slcExecution);

	/** Synchronously finds and executes an {@link ExecutionFlow}. */
	public void execute(RealizedFlow realizedFlow);
}
