package org.argeo.slc.execution;

import org.argeo.slc.deploy.Module;

public interface ExecutionModulesListener {
	public void executionModuleAdded(Module module,
			ExecutionContext executionContext);

	public void executionModuleRemoved(Module module,
			ExecutionContext executionContext);

	public void executionFlowAdded(Module module, ExecutionFlow executionFlow);

	public void executionFlowRemoved(Module module, ExecutionFlow executionFlow);
}
