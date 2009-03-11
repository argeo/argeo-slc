package org.argeo.slc.execution;

import org.argeo.slc.process.SlcExecution;

public interface ExecutionModule {
	public String getName();

	public String getVersion();

	public ExecutionModuleDescriptor getDescriptor();

	//TODO: remove
	public void execute(SlcExecution slcExecution);
	
	public void execute(ExecutionFlowDescriptor descriptor);
}
