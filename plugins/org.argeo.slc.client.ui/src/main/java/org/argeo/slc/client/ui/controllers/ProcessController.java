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
package org.argeo.slc.client.ui.controllers;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.argeo.slc.SlcException;
import org.argeo.slc.execution.ExecutionProcess;
import org.argeo.slc.execution.SlcAgent;
import org.argeo.slc.jcr.execution.JcrExecutionProcess;

/**
 * We use a separate class (not in UI components) so that it can be a singleton
 * in an application context.
 */
public class ProcessController {
	// private final static Log log =
	// LogFactory.getLog(ProcessController.class);
	// private Map<String, SlcAgentFactory> agentFactories = new HashMap<String,
	// SlcAgentFactory>();

	private SlcAgent agent;

	public ExecutionProcess process(Node processNode) {
		JcrExecutionProcess process = new JcrExecutionProcess(processNode);
		try {
			SlcAgent slcAgent = findAgent(processNode);
			if (slcAgent == null)
				throw new SlcException("Cannot find agent for " + processNode);
			slcAgent.process(process);
			return process;
		} catch (Exception e) {
			if (!process.getStatus().equals(ExecutionProcess.ERROR))
				process.setStatus(ExecutionProcess.ERROR);
			throw new SlcException("Cannot execute " + processNode, e);
		}
	}

	public void kill(Node processNode) {
		JcrExecutionProcess process = new JcrExecutionProcess(processNode);
		try {
			SlcAgent slcAgent = findAgent(processNode);
			if (slcAgent == null)
				throw new SlcException("Cannot find agent for " + processNode);
			slcAgent.kill(process.getUuid());
		} catch (Exception e) {
			if (!process.getStatus().equals(ExecutionProcess.ERROR))
				process.setStatus(ExecutionProcess.ERROR);
			throw new SlcException("Cannot execute " + processNode, e);
		}
	}

	/** Always return the default runtime agent */
	protected SlcAgent findAgent(Node processNode) throws RepositoryException {
		// we currently only deal with single agents
		// Node realizedFlowNode = processNode.getNode(SlcNames.SLC_FLOW);
		// NodeIterator nit = realizedFlowNode.getNodes();
		// if (nit.hasNext()) {
		// // TODO find a better way to determine which agent to use
		// // currently we check the agent of the first registered flow
		// Node firstRealizedFlow = nit.nextNode();
		// // we assume there is an nt:address
		// String firstFlowPath = firstRealizedFlow
		// .getNode(SlcNames.SLC_ADDRESS)
		// .getProperty(Property.JCR_PATH).getString();
		// Node flowNode = processNode.getSession().getNode(firstFlowPath);
		// String agentFactoryPath = SlcJcrUtils
		// .flowAgentFactoryPath(firstFlowPath);
		// if (!agentFactories.containsKey(agentFactoryPath))
		// throw new SlcException("No agent factory registered under "
		// + agentFactoryPath);
		// SlcAgentFactory agentFactory = agentFactories.get(agentFactoryPath);
		// Node agentNode = ((Node) flowNode
		// .getAncestor(SlcJcrUtils.AGENT_FACTORY_DEPTH + 1));
		// String agentUuid = agentNode.getProperty(SlcNames.SLC_UUID)
		// .getString();
		//
		// // process
		// return agentFactory.getAgent(agentUuid);
		// }

		return agent;
	}

	public void setAgent(SlcAgent agent) {
		this.agent = agent;
	}

	// public synchronized void register(SlcAgentFactory agentFactory,
	// Map<String, String> properties) {
	// String path = properties.get(SlcJcrConstants.PROPERTY_PATH);
	// if (log.isDebugEnabled())
	// log.debug("Agent factory registered under " + path);
	// agentFactories.put(path, agentFactory);
	// }
	//
	// public synchronized void unregister(SlcAgentFactory agentFactory,
	// Map<String, String> properties) {
	// String path = properties.get(SlcJcrConstants.PROPERTY_PATH);
	// if (log.isDebugEnabled())
	// log.debug("Agent factory unregistered from " + path);
	// agentFactories.remove(path);
	// }
}
