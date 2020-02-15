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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.security.Privilege;

import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcConstants;
import org.argeo.slc.SlcException;
import org.argeo.slc.SlcNames;
import org.argeo.slc.SlcTypes;
import org.argeo.slc.core.execution.DefaultAgent;
import org.argeo.slc.core.execution.ProcessThread;
import org.argeo.slc.execution.ExecutionModulesManager;
import org.argeo.slc.execution.ExecutionProcess;
import org.argeo.slc.jcr.SlcJcrConstants;

/** SLC VM agent synchronizing with a JCR repository. */
public class JcrAgent extends DefaultAgent implements SlcNames {
	// final static String ROLE_REMOTE = "ROLE_REMOTE";
	final static String NODE_REPO_URI = "argeo.node.repo.uri";

	private Repository repository;

	private String agentNodeName = "default";

	/*
	 * LIFECYCLE
	 */
	protected String initAgentUuid() {
		Session session = null;
		try {
			session = repository.login();

			String agentFactoryPath = getAgentFactoryPath();
			Node vmAgentFactoryNode = JcrUtils.mkdirsSafe(session, agentFactoryPath, SlcTypes.SLC_AGENT_FACTORY);
			JcrUtils.addPrivilege(session, SlcJcrConstants.SLC_BASE_PATH, SlcConstants.ROLE_SLC, Privilege.JCR_ALL);
			if (!vmAgentFactoryNode.hasNode(agentNodeName)) {
				String uuid = UUID.randomUUID().toString();
				Node agentNode = vmAgentFactoryNode.addNode(agentNodeName, SlcTypes.SLC_AGENT);
				agentNode.setProperty(SLC_UUID, uuid);
			}
			session.save();
			return vmAgentFactoryNode.getNode(agentNodeName).getProperty(SLC_UUID).getString();
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
	protected ProcessThread createProcessThread(ThreadGroup processesThreadGroup,
			ExecutionModulesManager modulesManager, ExecutionProcess process) {
		if (process instanceof JcrExecutionProcess)
			return new JcrProcessThread(processesThreadGroup, modulesManager, (JcrExecutionProcess) process);
		else
			return super.createProcessThread(processesThreadGroup, modulesManager, process);
	}

	/*
	 * UTILITIES
	 */
	public String getNodePath() {
		return getAgentFactoryPath() + '/' + getAgentNodeName();
	}

	public String getAgentFactoryPath() {
		try {
			Boolean isRemote = System.getProperty(NODE_REPO_URI) != null;
			String agentFactoryPath;
			if (isRemote) {
				InetAddress localhost = InetAddress.getLocalHost();
				agentFactoryPath = SlcJcrConstants.AGENTS_BASE_PATH + "/" + localhost.getCanonicalHostName();

				if (agentFactoryPath.equals(SlcJcrConstants.VM_AGENT_FACTORY_PATH))
					throw new SlcException("Unsupported hostname " + localhost.getCanonicalHostName());
			} else {// local
				agentFactoryPath = SlcJcrConstants.VM_AGENT_FACTORY_PATH;
			}
			return agentFactoryPath;
		} catch (UnknownHostException e) {
			throw new SlcException("Cannot find agent factory base path", e);
		}
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
