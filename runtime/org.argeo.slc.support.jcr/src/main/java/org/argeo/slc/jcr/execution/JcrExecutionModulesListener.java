package org.argeo.slc.jcr.execution;

import java.util.Arrays;
import java.util.Iterator;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.deploy.ModuleDescriptor;
import org.argeo.slc.execution.ExecutionFlowDescriptor;
import org.argeo.slc.execution.ExecutionModulesListener;
import org.argeo.slc.jcr.SlcJcrUtils;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;

/**
 * Synchronizes the local execution runtime with a JCR repository. For the time
 * being the state is completely reset from one start to another.
 */
public class JcrExecutionModulesListener implements ExecutionModulesListener {
	private final static Log log = LogFactory
			.getLog(JcrExecutionModulesListener.class);
	private JcrAgent agent;

	/**
	 * We don't use a thread bound session because many different threads will
	 * call this critical component and we don't want to login each time. We
	 * therefore rather protect access to this session via synchronized.
	 */
	private Session session;

	/*
	 * LIFECYCLE
	 */
	public void init() {
		clearAgent();
	}

	public void dispose() {
		clearAgent();
		session.logout();
	}

	protected synchronized void clearAgent() {
		try {
			Node agentNode = session.getNode(agent.getNodePath());
			for (NodeIterator nit = agentNode.getNodes(); nit.hasNext();)
				nit.nextNode().remove();
			session.save();
		} catch (RepositoryException e) {
			JcrUtils.discardQuietly(session);
			throw new SlcException("Cannot clear agent " + agent, e);
		}
	}

	/*
	 * EXECUTION MODULES LISTENER
	 */
	public synchronized void executionModuleAdded(
			ModuleDescriptor moduleDescriptor) {
		try {
			Node agentNode = session.getNode(agent.getNodePath());
			String moduleNodeName = SlcJcrUtils
					.getModuleNodeName(moduleDescriptor);
			Node moduleNode = agentNode.hasNode(moduleNodeName) ? agentNode
					.getNode(moduleNodeName) : agentNode
					.addNode(moduleNodeName);
			moduleNode.addMixin(SlcTypes.SLC_EXECUTION_MODULE);
			moduleNode.setProperty(SlcNames.SLC_NAME,
					moduleDescriptor.getName());
			moduleNode.setProperty(SlcNames.SLC_VERSION,
					moduleDescriptor.getVersion());
			moduleNode.setProperty(Property.JCR_TITLE,
					moduleDescriptor.getTitle());
			moduleNode.setProperty(Property.JCR_DESCRIPTION,
					moduleDescriptor.getDescription());
			session.save();
		} catch (RepositoryException e) {
			JcrUtils.discardQuietly(session);
			throw new SlcException("Cannot add module " + moduleDescriptor, e);
		}

	}

	public synchronized void executionModuleRemoved(
			ModuleDescriptor moduleDescriptor) {
		try {
			String moduleName = SlcJcrUtils.getModuleNodeName(moduleDescriptor);
			Node agentNode = session.getNode(agent.getNodePath());
			if (agentNode.hasNode(moduleName))
				agentNode.getNode(moduleName).remove();
			agentNode.getSession().save();
		} catch (RepositoryException e) {
			throw new SlcException("Cannot remove module " + moduleDescriptor,
					e);
		}
	}

	public synchronized void executionFlowAdded(ModuleDescriptor module,
			ExecutionFlowDescriptor executionFlow) {
		try {
			Node agentNode = session.getNode(agent.getNodePath());
			Node moduleNode = agentNode.getNode(SlcJcrUtils
					.getModuleNodeName(module));
			String relativePath = getExecutionFlowRelativePath(executionFlow);
			Node flowNode = null;
			if (!moduleNode.hasNode(relativePath)) {
				Iterator<String> names = Arrays.asList(relativePath.split("/"))
						.iterator();
				// create intermediary paths
				Node currNode = moduleNode;
				while (names.hasNext()) {
					String name = names.next();
					if (currNode.hasNode(name))
						currNode = currNode.getNode(name);
					else {
						if (names.hasNext())
							currNode = currNode.addNode(name);
						else
							flowNode = currNode.addNode(name,
									SlcTypes.SLC_EXECUTION_FLOW);
					}
				}
				flowNode.setProperty(SlcNames.SLC_NAME, executionFlow.getName());
				session.save();
			} else {
				flowNode = moduleNode.getNode(relativePath);
			}

			if (log.isTraceEnabled())
				log.trace("Flow " + executionFlow + " added to JCR");
		} catch (RepositoryException e) {
			JcrUtils.discardQuietly(session);
			throw new SlcException("Cannot add flow " + executionFlow
					+ " from module " + module, e);
		}

	}

	public synchronized void executionFlowRemoved(ModuleDescriptor module,
			ExecutionFlowDescriptor executionFlow) {
		try {
			Node agentNode = session.getNode(agent.getNodePath());
			Node moduleNode = agentNode.getNode(SlcJcrUtils
					.getModuleNodeName(module));
			String relativePath = getExecutionFlowRelativePath(executionFlow);
			if (!moduleNode.hasNode(relativePath))
				moduleNode.getNode(relativePath).remove();
			agentNode.getSession().save();
		} catch (RepositoryException e) {
			throw new SlcException("Cannot remove flow " + executionFlow
					+ " from module " + module, e);
		}
	}

	/*
	 * UTILITIES
	 */
	/** @return the relative path, never starts with '/' */
	@SuppressWarnings("deprecation")
	protected String getExecutionFlowRelativePath(
			ExecutionFlowDescriptor executionFlow) {
		String relativePath = executionFlow.getPath() == null ? executionFlow
				.getName() : executionFlow.getPath() + '/'
				+ executionFlow.getName();
		// we assume that it is more than one char long
		if (relativePath.charAt(0) == '/')
			relativePath = relativePath.substring(1);
		return relativePath;
	}

	/*
	 * BEAN
	 */
	public void setAgent(JcrAgent agent) {
		this.agent = agent;
	}

	/** Expects a non-shared session with admin authorization */
	public void setSession(Session session) {
		this.session = session;
	}

}
