package org.argeo.slc.dao.runtime;

import java.util.List;

import org.argeo.slc.runtime.SlcAgentDescriptor;

public interface SlcAgentDescriptorDao {
	public void create(SlcAgentDescriptor slcAgentDescriptor);

	public List<SlcAgentDescriptor> listSlcAgentDescriptors();

}
