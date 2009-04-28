package org.argeo.slc.runtime;

import java.util.List;

import org.argeo.slc.execution.ExecutionModuleDescriptor;
import org.argeo.slc.process.SlcExecution;

/** A local agent, able to run SLC Execution locally. */
public interface SlcAgent {
	public ExecutionModuleDescriptor getExecutionModuleDescriptor(
			String moduleName, String version);

	public List<ExecutionModuleDescriptor> listExecutionModuleDescriptors();

	public void runSlcExecution(SlcExecution slcExecution);

	/** @return true if still alive. */
	public boolean ping();
}
