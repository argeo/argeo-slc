package org.argeo.slc.execution;


public interface ExecutionModule {
	public String getName();

	public String getVersion();
	
	public ExecutionContext getExecutionContext();

	public ExecutionModuleDescriptor getDescriptor();

	public void execute(ExecutionFlowDescriptor descriptor);
}
