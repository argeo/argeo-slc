/*
 * Copyright (C) 2007-2012 Argeo GmbH
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
package org.argeo.slc.jcr.execution;

import java.util.UUID;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.core.execution.DefaultAgent;
import org.argeo.slc.core.execution.ProcessThread;
import org.argeo.slc.execution.ExecutionModulesManager;
import org.argeo.slc.execution.ExecutionProcess;
import org.argeo.slc.jcr.SlcJcrConstants;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;

/** SLC VM agent synchronizing with a JCR repository. */
public class JcrAgent extends DefaultAgent implements SlcNames {
	private Repository repository;

	private String agentNodeName = "default";

	/*
	 * LIFECYCLE
	 */
	protected String initAgentUuid() {
		Session session = null;
		try {
			session = repository.login();
			Node vmAgentFactoryNode = JcrUtils.mkdirsSafe(session,
					SlcJcrConstants.VM_AGENT_FACTORY_PATH,
					SlcTypes.SLC_AGENT_FACTORY);
			if (!vmAgentFactoryNode.hasNode(agentNodeName)) {
				String uuid = UUID.randomUUID().toString();
				Node agentNode = vmAgentFactoryNode.addNode(agentNodeName,
						SlcTypes.SLC_AGENT);
				agentNode.setProperty(SLC_UUID, uuid);
			}
			session.save();
			return vmAgentFactoryNode.getNode(agentNodeName)
					.getProperty(SLC_UUID).getString();
		} catch (RepositoryException e) {
			JcrUtils.discardQuietly(session);
			throw new SlcException("Cannot find JCR agent UUID", e);
		} finally {
			JcrUtils.logoutQuietly(session);
		}
	}

	@Override
	public void destroy() {
		super.destroy();
	}

	/*
	 * SLC AGENT
	 */
	@Override
	protected ProcessThread createProcessThread(
			ThreadGroup processesThreadGroup,
			ExecutionModulesManager modulesManager, ExecutionProcess process) {
		if (process instanceof JcrProcessThread)
			return new JcrProcessThread(processesThreadGroup, modulesManager,
					(JcrExecutionProcess) process);
		else
			return super.createProcessThread(processesThreadGroup,
					modulesManager, process);
	}

	/*
	 * UTILITIES
	 */
	public String getNodePath() {
		return SlcJcrConstants.VM_AGENT_FACTORY_PATH + '/' + getAgentNodeName();
	}

	/*
	 * BEAN
	 */
	public String getAgentNodeName() {
		return agentNodeName;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public void setAgentNodeName(String agentNodeName) {
		this.agentNodeName = agentNodeName;
	}

}
