package org.argeo.slc.execution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.argeo.slc.SlcException;
import org.argeo.slc.deploy.ModuleDescriptor;

/** Describes the information required to launch a flow */
public class ExecutionModuleDescriptor extends ModuleDescriptor {
	/** Metadata header identifying an SLC execution module */
	public final static String SLC_EXECUTION_MODULE = "SLC-ExecutionModule";

	private static final long serialVersionUID = -2394473464513029512L;
	private List<ExecutionSpec> executionSpecs = new ArrayList<ExecutionSpec>();
	private List<ExecutionFlowDescriptor> executionFlows = new ArrayList<ExecutionFlowDescriptor>();

	public List<ExecutionSpec> getExecutionSpecs() {
		return executionSpecs;
	}

	public List<ExecutionFlowDescriptor> getExecutionFlows() {
		return executionFlows;
	}

	/**
	 * Returns a new {@link ExecutionModuleDescriptor} that can be used to build
	 * a {@link RealizedFlow}.
	 */
	public ExecutionFlowDescriptor cloneFlowDescriptor(String name) {
		ExecutionFlowDescriptor res = null;
		for (ExecutionFlowDescriptor efd : executionFlows) {
			if (efd.getName().equals(name)
					|| ("/" + efd.getName()).equals(name)) {
				try {
					res = (ExecutionFlowDescriptor) efd.clone();
				} catch (CloneNotSupportedException e) {
					throw new SlcException("Cannot clone " + efd, e);
				}
			}
		}
		if (res == null)
			throw new SlcException("Flow " + name + " not found.");
		return res;
	}

	public RealizedFlow asRealizedFlow(String flow, Map<String, Object> values) {
		RealizedFlow realizedFlow = new RealizedFlow();
		realizedFlow.setFlowDescriptor(cloneFlowDescriptor(flow));
		realizedFlow.setModuleName(getName());
		realizedFlow.setModuleVersion(getVersion());
		realizedFlow.getFlowDescriptor().getValues().putAll(values);
		return realizedFlow;
	}

	public void setExecutionSpecs(List<ExecutionSpec> executionSpecs) {
		this.executionSpecs = executionSpecs;
	}

	public void setExecutionFlows(List<ExecutionFlowDescriptor> executionFlows) {
		this.executionFlows = executionFlows;
	}
}
