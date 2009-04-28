package org.argeo.slc.services.impl.runtime;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.dao.runtime.SlcAgentDescriptorDao;
import org.argeo.slc.runtime.SlcAgent;
import org.argeo.slc.runtime.SlcAgentDescriptor;
import org.argeo.slc.runtime.SlcAgentFactory;
import org.argeo.slc.services.runtime.AgentService;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class AgentServiceImpl implements AgentService, InitializingBean,
		DisposableBean {
	private final static Log log = LogFactory.getLog(AgentServiceImpl.class);

	private final SlcAgentDescriptorDao slcAgentDescriptorDao;
	private final SlcAgentFactory agentFactory;

	private Long pingCycle = 60000l;

	private Boolean pingThreadActive = true;

	public AgentServiceImpl(SlcAgentDescriptorDao slcAgentDescriptorDao,
			SlcAgentFactory agentFactory) {
		this.slcAgentDescriptorDao = slcAgentDescriptorDao;
		this.agentFactory = agentFactory;
	}

	public void register(SlcAgentDescriptor slcAgentDescriptor) {
		slcAgentDescriptorDao.create(slcAgentDescriptor);
		log.info("Registered agent #" + slcAgentDescriptor.getUuid());
	}

	public void unregister(SlcAgentDescriptor slcAgentDescriptor) {
		slcAgentDescriptorDao.delete(slcAgentDescriptor);
		log.info("Unregistered agent #" + slcAgentDescriptor.getUuid());
	}

	public void afterPropertiesSet() throws Exception {
		if (pingCycle > 0)
			new PingThread().start();
	}

	public synchronized void destroy() throws Exception {
		pingThreadActive = false;
		notifyAll();
	}

	public void setPingCycle(Long pingCycle) {
		this.pingCycle = pingCycle;
	}

	protected class PingThread extends Thread {
		public void run() {
			while (pingThreadActive) {
				List<SlcAgentDescriptor> lst = slcAgentDescriptorDao
						.listSlcAgentDescriptors();
				List<String> agentIds = new ArrayList<String>();
				for (SlcAgentDescriptor ad : lst)
					agentIds.add(ad.getUuid());

				if (log.isDebugEnabled())
					log.debug("Ping " + agentIds.size() + " agent.");
				for (String agentId : agentIds) {
					SlcAgent agent = agentFactory.getAgent(agentId);
					if (!agent.ping()) {
						log.info("Agent " + agentId + " did not reply to ping,"
								+ " removing its descriptor...");
						slcAgentDescriptorDao.delete(agentId);
					}
				}

				lst = slcAgentDescriptorDao.listSlcAgentDescriptors();
				agentIds = new ArrayList<String>();
				for (SlcAgentDescriptor ad : lst)
					agentIds.add(ad.getUuid());
				agentFactory.pingAll(agentIds);

				synchronized (AgentServiceImpl.this) {
					try {
						AgentServiceImpl.this.wait(pingCycle);
					} catch (InterruptedException e) {
						// silent
					}
				}
			}
		}

	}
}
