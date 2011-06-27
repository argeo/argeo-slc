package org.argeo.slc.client.ui.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.execution.ExecutionProcess;
import org.argeo.slc.jcr.SlcJcrConstants;
import org.argeo.slc.jcr.SlcJcrUtils;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.execution.JcrExecutionProcess;
import org.argeo.slc.runtime.SlcAgent;
import org.argeo.slc.runtime.SlcAgentFactory;

/**
 * We use a separate class (not in UI components) so that it can be a singleton
 * in an application context.
 */
public class ProcessController {
	private final static Log log = LogFactory.getLog(ProcessController.class);
	private Map<String, SlcAgentFactory> agentFactories = new HashMap<String, SlcAgentFactory>();

	public void process(Node processNode) {
		JcrExecutionProcess process = new JcrExecutionProcess(processNode);
		try {
			// we currently only deal with single agents
			Node realizedFlowNode = processNode.getNode(SlcNames.SLC_FLOW);
			NodeIterator nit = realizedFlowNode.getNodes();
			if (nit.hasNext()) {
				// TODO find a better way to determine which agent to use
				// currently we check the agent of the first registered flow
				Node firstRealizedFlow = nit.nextNode();
				// we assume there is an nt:address
				String firstFlowPath = firstRealizedFlow
						.getNode(SlcNames.SLC_ADDRESS)
						.getProperty(Property.JCR_PATH).getString();
				Node flowNode = processNode.getSession().getNode(firstFlowPath);
				String agentFactoryPath = SlcJcrUtils
						.flowAgentFactoryPath(firstFlowPath);
				if (!agentFactories.containsKey(agentFactoryPath))
					throw new SlcException("No agent factory registered under "
							+ agentFactoryPath);
				SlcAgentFactory agentFactory = agentFactories
						.get(agentFactoryPath);
				Node agentNode = ((Node) flowNode
						.getAncestor(SlcJcrUtils.AGENT_FACTORY_DEPTH + 1));
				String agentUuid = agentNode.getProperty(SlcNames.SLC_UUID)
						.getString();

				// process
				SlcAgent slcAgent = agentFactory.getAgent(agentUuid);
				slcAgent.process(process);
			}
		} catch (Exception e) {
			if (!process.getStatus().equals(ExecutionProcess.ERROR))
				process.setStatus(ExecutionProcess.ERROR);
			throw new SlcException("Cannot execute " + processNode, e);
		}
	}

	public synchronized void register(SlcAgentFactory agentFactory,
			Map<String, String> properties) {
		String path = properties.get(SlcJcrConstants.PROPERTY_PATH);
		if (log.isDebugEnabled())
			log.debug("Agent factory registered under " + path);
		agentFactories.put(path, agentFactory);
	}

	public synchronized void unregister(SlcAgentFactory agentFactory,
			Map<String, String> properties) {
		String path = properties.get(SlcJcrConstants.PROPERTY_PATH);
		if (log.isDebugEnabled())
			log.debug("Agent factory unregistered from " + path);
		agentFactories.remove(path);
	}
}
