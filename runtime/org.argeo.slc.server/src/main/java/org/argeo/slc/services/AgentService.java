package org.argeo.slc.services;

import org.argeo.slc.runtime.SlcAgentDescriptor;

public interface AgentService {
	public void register(SlcAgentDescriptor slcAgentDescriptor);

	public void unregister(SlcAgentDescriptor slcAgentDescriptor);
}
