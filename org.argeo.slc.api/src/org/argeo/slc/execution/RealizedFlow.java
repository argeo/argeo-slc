package org.argeo.slc.execution;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.argeo.slc.DefaultNameVersion;
import org.argeo.slc.NameVersion;

/** A fully configured execution flow, ready to be executed. */
public class RealizedFlow implements Serializable {
	private static final long serialVersionUID = 1L;

	private String moduleName;
	private String moduleVersion;
	private ExecutionFlowDescriptor flowDescriptor;

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public NameVersion getModuleNameVersion() {
		return new DefaultNameVersion(getModuleName(), getModuleVersion());
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

	/** Create a simple realized flow */
	public static RealizedFlow create(String module, String version,
			String flowName, Map<String, String> args) {
		final RealizedFlow realizedFlow = new RealizedFlow();
		realizedFlow.setModuleName(module);
		// TODO deal with version
		if (version == null)
			version = "0.0.0";
		realizedFlow.setModuleVersion(version);
		ExecutionFlowDescriptor efd = new ExecutionFlowDescriptor();
		efd.setName(flowName);

		// arguments
		if (args != null && args.size() > 0) {
			Map<String, Object> values = new HashMap<String, Object>();
			for (String key : args.keySet()) {
				String value = args.get(key);
				values.put(key, value);
			}
			efd.setValues(values);
		}

		realizedFlow.setFlowDescriptor(efd);
		return realizedFlow;
	}
}
