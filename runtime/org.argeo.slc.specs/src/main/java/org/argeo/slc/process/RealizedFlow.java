package org.argeo.slc.process;

import java.io.Serializable;

import org.argeo.slc.execution.ExecutionFlowDescriptor;
import org.argeo.slc.execution.ExecutionSpec;

public class RealizedFlow implements Serializable {
	private static final long serialVersionUID = 1L;

	private String moduleName;
	private String moduleVersion;
	private ExecutionFlowDescriptor flowDescriptor;
	private ExecutionSpec executionSpec;

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getModuleVersion() {
		return moduleVersion;
	}

	public void setModuleVersion(String moduleVersion) {
		this.moduleVersion = moduleVersion;
	}

	public ExecutionFlowDescriptor getFlowDescriptor() {
		return flowDescriptor;
	}

	public void setFlowDescriptor(ExecutionFlowDescriptor flowDescriptor) {
		this.flowDescriptor = flowDescriptor;
	}

	public ExecutionSpec getExecutionSpec() {
		return executionSpec;
	}

	public void setExecutionSpec(ExecutionSpec executionSpec) {
		this.executionSpec = executionSpec;
	}

}
