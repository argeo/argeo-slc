package org.argeo.slc.execution;

import java.util.List;

import org.argeo.slc.process.SlcExecution;

public interface ExecutionModulesManager {
	public ExecutionModuleDescriptor getExecutionModuleDescriptor(
			String moduleName, String version);

	public List<ExecutionModule> listExecutionModules();

	public void process(SlcExecution slcExecution);
}
