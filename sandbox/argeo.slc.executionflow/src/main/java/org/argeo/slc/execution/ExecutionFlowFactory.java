package org.argeo.slc.execution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.argeo.slc.process.Executable;

public class ExecutionFlowFactory {
	private List<Executable> executables = new ArrayList<Executable>();

	
	public ExecutionFlow createExecutionFlow(Map<String, Object> attributes){
		SimpleExecutionFlow executionFlow = new SimpleExecutionFlow();
		executionFlow.setExecutables(executables);
		return executionFlow;
	}


	public void setExecutables(List<Executable> executables) {
		this.executables = executables;
	}
	
	
}
