/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.argeo.slc.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.dao.runtime.SlcAgentDescriptorDao;
import org.argeo.slc.runtime.SlcAgent;
import org.argeo.slc.runtime.SlcAgentDescriptor;
import org.argeo.slc.runtime.SlcAgentFactory;
import org.argeo.slc.services.AgentService;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class AgentServiceImpl implements AgentService, InitializingBean,
		DisposableBean {
	private final static Log log = LogFactory.getLog(AgentServiceImpl.class);

	private final SlcAgentDescriptorDao slcAgentDescriptorDao;
	private final SlcAgentFactory agentFactory;

	private Executor systemExecutor;

	private Long pingCycle = 20000l;

	private Boolean pingThreadActive = true;

	public AgentServiceImpl(SlcAgentDescriptorDao slcAgentDescriptorDao,
			SlcAgentFactory agentFactory) {
		this.slcAgentDescriptorDao = slcAgentDescriptorDao;
		this.agentFactory = agentFactory;
	}

	public void register(SlcAgentDescriptor slcAgentDescriptor) {
		if (slcAgentDescriptorDao.getAgentDescriptor(slcAgentDescriptor
				.getUuid()) == null)
			slcAgentDescriptorDao.create(slcAgentDescriptor);
		log.info("Registered agent #" + slcAgentDescriptor.getUuid());
	}

	public void unregister(SlcAgentDescriptor slcAgentDescriptor) {
		slcAgentDescriptorDao.delete(slcAgentDescriptor);
		log.info("Unregistered agent #" + slcAgentDescriptor.getUuid());
	}

	public void afterPropertiesSet() throws Exception {
		// if (pingCycle > 0)
		// new PingThread().start();
		if (pingCycle > 0) {
			Thread authenticatedThread = new Thread("SLC Agents Ping") {
				public void run() {
					systemExecutor.execute(new AgentsPing());
				}
			};
			authenticatedThread.start();

		}
	}

	public synchronized void destroy() throws Exception {
		pingThreadActive = false;
		notifyAll();
	}

	public void setPingCycle(Long pingCycle) {
		this.pingCycle = pingCycle;
	}

	public void setSystemExecutor(Executor securityService) {
		this.systemExecutor = securityService;
	}

	protected class AgentsPing implements Runnable {
		public void run() {

			// FIXME: temporary hack so that the ping starts after the server
			// has been properly started.
			try {
				Thread.sleep(10 * 1000);
			} catch (InterruptedException e1) {
				// silent
			}

			log.info("Start pinging agents.");
			while (pingThreadActive) {
				List<SlcAgentDescriptor> lst = slcAgentDescriptorDao
						.listSlcAgentDescriptors();
				List<String> agentIds = new ArrayList<String>();
				for (SlcAgentDescriptor ad : lst)
					agentIds.add(ad.getUuid());

				if (log.isTraceEnabled())
					log.trace("Ping " + agentIds.size() + " agent(s).");
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
			log.info("Stopped pinging agents.");
		}

	}

}
