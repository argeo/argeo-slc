package org.argeo.slc.runtime;

import java.util.List;

import org.argeo.slc.execution.ExecutionModuleDescriptor;

/** A local agent, able to run SLC Execution locally. */
public interface SlcAgent {
	public ExecutionModuleDescriptor getExecutionModuleDescriptor(
			String moduleName, String version);

	public List<ExecutionModuleDescriptor> listExecutionModuleDescriptors();

}
