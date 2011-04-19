package org.argeo.slc.client.ui.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.jcr.SlcJcrConstants;
import org.argeo.slc.jcr.SlcJcrUtils;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.execution.JcrExecutionProcess;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.runtime.SlcAgent;
import org.argeo.slc.runtime.SlcAgentFactory;
import org.argeo.slc.services.SlcExecutionService;

public class ProcessController {
	private final static Log log = LogFactory.getLog(ProcessController.class);
	private SlcExecutionService slcExecutionService;
	private Map<String, SlcAgentFactory> agentFactories = new HashMap<String, SlcAgentFactory>();

	public void execute(SlcAgent agent, SlcExecution slcExecution) {
		slcExecutionService.newExecution(slcExecution);
		agent.process(slcExecution);
		if (log.isDebugEnabled())
			log.debug("SlcExcution " + slcExecution.getUuid()
					+ " launched on Agent " + agent.toString());
	}

	public void process(Node processNode) {
		try {
			// we currently only deal with single agents
			Node flowNode = processNode.getNode(SlcNames.SLC_FLOW);
			NodeIterator nit = flowNode.getNodes();
			if (nit.hasNext()) {
				Node firstFlow = nit.nextNode();
				// we assume there is an nt:address
				String firstFlowPath = firstFlow.getNode(SlcNames.SLC_ADDRESS)
						.getProperty(Property.JCR_PATH).getString();
				String agentFactoryPath = SlcJcrUtils
						.flowAgentFactoryPath(firstFlowPath);
				if (!agentFactories.containsKey(agentFactoryPath))
					throw new SlcException("No agent factory registered under "
							+ agentFactoryPath);
				SlcAgentFactory agentFactory = agentFactories
						.get(agentFactoryPath);
				String agentUuid = ((Node) processNode
						.getAncestor(SlcJcrUtils.AGENT_FACTORY_DEPTH + 1))
						.getName();

				// process
				SlcAgent slcAgent = agentFactory.getAgent(agentUuid);
				slcAgent.process(new JcrExecutionProcess(processNode));
			}
		} catch (RepositoryException e) {
			throw new SlcException("Cannot execute " + processNode, e);
		}
	}

	public void setSlcExecutionService(SlcExecutionService slcExecutionService) {
		this.slcExecutionService = slcExecutionService;
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
