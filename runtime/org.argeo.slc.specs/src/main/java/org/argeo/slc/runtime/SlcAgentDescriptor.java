package org.argeo.slc.runtime;

import java.util.ArrayList;
import java.util.List;

import org.argeo.slc.execution.ExecutionModuleDescriptor;

public class SlcAgentDescriptor implements Cloneable {
	private String uuid;
	private String host;
	private List<ExecutionModuleDescriptor> moduleDescriptors = new ArrayList<ExecutionModuleDescriptor>();

	public SlcAgentDescriptor() {

	}

	public SlcAgentDescriptor(SlcAgentDescriptor template) {
		uuid = template.uuid;
		host = template.host;
		moduleDescriptors.addAll(template.moduleDescriptors);
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public List<ExecutionModuleDescriptor> getModuleDescriptors() {
		return moduleDescriptors;
	}

	public void setModuleDescriptors(
			List<ExecutionModuleDescriptor> modulesDescriptors) {
		this.moduleDescriptors = modulesDescriptors;
	}
}
