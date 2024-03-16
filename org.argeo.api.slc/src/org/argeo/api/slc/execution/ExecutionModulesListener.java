package org.argeo.api.slc.execution;

import org.argeo.api.slc.deploy.ModuleDescriptor;

/** Listen to events on execution modules. */
public interface ExecutionModulesListener {
	public void executionModuleAdded(ModuleDescriptor moduleDescriptor);

	public void executionModuleRemoved(ModuleDescriptor moduleDescriptor);

	public void executionFlowAdded(ModuleDescriptor moduleDescriptor,
			ExecutionFlowDescriptor executionFlowDescriptor);

	public void executionFlowRemoved(ModuleDescriptor moduleDescriptor,
			ExecutionFlowDescriptor executionFlowDescriptor);
}
