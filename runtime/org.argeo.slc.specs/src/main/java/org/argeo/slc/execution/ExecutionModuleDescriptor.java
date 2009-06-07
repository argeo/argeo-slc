package org.argeo.slc.execution;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ExecutionModuleDescriptor implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String version;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
