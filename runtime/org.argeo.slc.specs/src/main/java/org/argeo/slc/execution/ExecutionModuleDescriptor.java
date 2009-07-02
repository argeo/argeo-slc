package org.argeo.slc.execution;

import java.util.ArrayList;
import java.util.List;

import org.argeo.slc.deploy.ModuleDescriptor;

public class ExecutionModuleDescriptor extends ModuleDescriptor {
	private static final long serialVersionUID = 1L;

	private List<ExecutionSpec> executionSpecs = new ArrayList<ExecutionSpec>();
	private List<ExecutionFlowDescriptor> executionFlows = new ArrayList<ExecutionFlowDescriptor>();

	public List<ExecutionSpec> getExecutionSpecs() {
		return executionSpecs;
	}

	public List<ExecutionFlowDescriptor> getExecutionFlows() {
		return executionFlows;
	}

	public void setExecutionSpecs(List<ExecutionSpec> executionSpecs) {
		this.executionSpecs = executionSpecs;
	}

	public void setExecutionFlows(List<ExecutionFlowDescriptor> executionFlows) {
		this.executionFlows = executionFlows;
	}
}
