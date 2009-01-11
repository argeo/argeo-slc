package org.argeo.slc.services.impl.runtime;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.dao.runtime.SlcAgentDescriptorDao;
import org.argeo.slc.runtime.SlcAgentDescriptor;

public class AgentServiceImpl {
	private final static Log log = LogFactory.getLog(AgentServiceImpl.class);

	private final SlcAgentDescriptorDao slcAgentDescriptorDao;

	public AgentServiceImpl(SlcAgentDescriptorDao slcAgentDescriptorDao) {
		this.slcAgentDescriptorDao = slcAgentDescriptorDao;
	}

	public void register(SlcAgentDescriptor slcAgentDescriptor) {
		slcAgentDescriptorDao.create(slcAgentDescriptor);
		log.info("Registered agent #" + slcAgentDescriptor.getUuid());
	}

}
