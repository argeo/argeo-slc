package org.argeo.slc.dao.runtime;

import java.util.List;

import org.argeo.slc.runtime.SlcAgentDescriptor;

public interface SlcAgentDescriptorDao {
	public void create(SlcAgentDescriptor slcAgentDescriptor);

	public void delete(SlcAgentDescriptor slcAgentDescriptor);

	public void delete(String agentId);

	public List<SlcAgentDescriptor> listSlcAgentDescriptors();

	public SlcAgentDescriptor getAgentDescriptor(String agentId);
}
