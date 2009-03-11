package org.argeo.slc.execution.old;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.argeo.slc.core.execution.DefaultExecutionFlow;
import org.argeo.slc.execution.Executable;
import org.argeo.slc.execution.ExecutionFlow;

public class ExecutionFlowFactory {
	private List<Executable> executables = new ArrayList<Executable>();

	
	public ExecutionFlow createExecutionFlow(Map<String, Object> attributes){
		DefaultExecutionFlow executionFlow = new DefaultExecutionFlow();
		executionFlow.setExecutables(executables);
		return executionFlow;
	}


	public void setExecutables(List<Executable> executables) {
		this.executables = executables;
	}
	
	
}
