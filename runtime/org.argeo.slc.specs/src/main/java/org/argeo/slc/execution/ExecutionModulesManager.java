package org.argeo.slc.execution;

import java.util.List;

public interface ExecutionModulesManager {
	public ExecutionModuleDescriptor getExecutionModuleDescriptor(
			String moduleName, String version);

	public List<ExecutionModule> listExecutionModules();
}
