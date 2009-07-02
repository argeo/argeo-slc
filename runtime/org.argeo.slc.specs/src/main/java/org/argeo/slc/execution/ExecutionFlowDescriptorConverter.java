package org.argeo.slc.execution;

import java.util.Map;

public interface ExecutionFlowDescriptorConverter {
	public Map<String, Object> convertValues(
			ExecutionFlowDescriptor executionFlowDescriptor);

	public void addFlowsToDescriptor(ExecutionModuleDescriptor md,
			Map<String, ExecutionFlow> executionFlows);
}
