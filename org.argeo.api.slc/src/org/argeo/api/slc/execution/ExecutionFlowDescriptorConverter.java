package org.argeo.api.slc.execution;

import java.util.Map;

/**
 * Maps back and forth between {@link ExecutionFlowDescriptor} and
 * {@link ExecutionFlow}
 */
public interface ExecutionFlowDescriptorConverter {
	public Map<String, Object> convertValues(
			ExecutionFlowDescriptor executionFlowDescriptor);

	public void addFlowsToDescriptor(ExecutionModuleDescriptor md,
			Map<String, ExecutionFlow> executionFlows);

	public ExecutionFlowDescriptor getExecutionFlowDescriptor(
			ExecutionFlow executionFlow);
}
