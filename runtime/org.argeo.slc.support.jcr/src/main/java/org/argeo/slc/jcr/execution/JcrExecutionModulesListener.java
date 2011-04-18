package org.argeo.slc.jcr.execution;

import java.util.Arrays;
import java.util.Iterator;

import javax.jcr.Node;
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
import org.argeo.slc.jcr.SlcJcrConstants;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.argeo.slc.runtime.SlcAgent;

/**
 * Synchronizes the local execution runtime with a JCR repository. For the time
 * being the state is completely reset from one start to another.
 */
public class JcrExecutionModulesListener implements ExecutionModulesListener {
	private final static Log log = LogFactory
			.getLog(JcrExecutionModulesListener.class);

	private Session session;
	private SlcAgent agent;

	public void init() {
		try {
			String modulesPath = getExecutionModulesPath();
			// clean up previous state
			if (session.nodeExists(modulesPath))
				session.getNode(modulesPath).remove();
			JcrUtils.mkdirs(session, modulesPath);
			session.save();
		} catch (RepositoryException e) {
			throw new SlcException(
					"Cannot initialize JCR execution module listener", e);
		} finally {
			JcrUtils.discardQuietly(session);
		}
	}

	public void dispose() {
		try {
			String modulesPath = getExecutionModulesPath();
			// clean up previous state
			if (session.nodeExists(modulesPath))
				session.getNode(modulesPath).remove();
			session.save();
		} catch (RepositoryException e) {
			throw new SlcException(
					"Cannot dispose JCR execution module listener", e);
		} finally {
			JcrUtils.discardQuietly(session);
		}
	}

	public void executionModuleAdded(ModuleDescriptor moduleDescriptor) {
		try {
			Node base = session.getNode(getExecutionModulesPath());
			Node moduleName = base.hasNode(moduleDescriptor.getName()) ? base
					.getNode(moduleDescriptor.getName()) : base
					.addNode(moduleDescriptor.getName());
			Node moduleVersion = moduleName.hasNode(moduleDescriptor
					.getVersion()) ? moduleName.getNode(moduleDescriptor
					.getVersion()) : moduleName.addNode(moduleDescriptor
					.getVersion());
			moduleVersion.addMixin(SlcTypes.SLC_MODULE);
			moduleVersion.setProperty(SlcNames.SLC_NAME,
					moduleDescriptor.getName());
			moduleVersion.setProperty(SlcNames.SLC_VERSION,
					moduleDescriptor.getVersion());
			moduleVersion.setProperty(Property.JCR_TITLE,
					moduleDescriptor.getTitle());
			moduleVersion.setProperty(Property.JCR_DESCRIPTION,
					moduleDescriptor.getDescription());
			session.save();
		} catch (RepositoryException e) {
			throw new SlcException("Cannot add module " + moduleDescriptor, e);
		}

	}

	public void executionModuleRemoved(ModuleDescriptor moduleDescriptor) {
		try {
			Node base = session.getNode(getExecutionModulesPath());
			if (base.hasNode(moduleDescriptor.getName())) {
				Node moduleName = base.getNode(moduleDescriptor.getName());
				if (moduleName.hasNode(moduleDescriptor.getVersion()))
					moduleName.getNode(moduleDescriptor.getVersion()).remove();
				if (!moduleName.hasNodes())
					moduleName.remove();
				session.save();
			}
		} catch (RepositoryException e) {
			throw new SlcException("Cannot remove module " + moduleDescriptor,
					e);
		}
	}

	public void executionFlowAdded(ModuleDescriptor module,
			ExecutionFlowDescriptor executionFlow) {
		String path = getExecutionFlowPath(module, executionFlow);
		try {
			Node flowNode = null;
			if (!session.nodeExists(path)) {
				Node base = session.getNode(getExecutionModulesPath());
				Node moduleNode = base.getNode(module.getName() + '/'
						+ module.getVersion());
				String relativePath = getExecutionFlowRelativePath(executionFlow);
				Iterator<String> names = Arrays.asList(relativePath.split("/"))
						.iterator();
				Node currNode = moduleNode;
				while (names.hasNext()) {
					String name = names.next();
					if (currNode.hasNode(name))
						currNode = currNode.getNode(name);
					else {
						if (names.hasNext())
							currNode = currNode.addNode(name);
						else
							flowNode = currNode.addNode(name);
					}
				}
				flowNode.addMixin(SlcTypes.SLC_EXECUTION_FLOW);
				flowNode.setProperty(SlcNames.SLC_NAME, executionFlow.getName());
				session.save();
			} else {
				flowNode = session.getNode(path);
			}

			if (log.isTraceEnabled())
				log.trace("Flow " + executionFlow + " added to JCR");
		} catch (RepositoryException e) {
			throw new SlcException("Cannot add flow " + executionFlow
					+ " from module " + module, e);
		}

	}

	public void executionFlowRemoved(ModuleDescriptor module,
			ExecutionFlowDescriptor executionFlow) {
		String path = getExecutionFlowPath(module, executionFlow);
		try {
			if (session.nodeExists(path)) {
				Node flowNode = session.getNode(path);
				flowNode.remove();
				session.save();
			}
		} catch (RepositoryException e) {
			throw new SlcException("Cannot remove flow " + executionFlow
					+ " from module " + module, e);
		}
	}

	protected String getExecutionFlowPath(ModuleDescriptor module,
			ExecutionFlowDescriptor executionFlow) {
		String relativePath = getExecutionFlowRelativePath(executionFlow);
		return getExecutionModulesPath() + '/' + module.getName() + '/'
				+ module.getVersion() + '/' + relativePath;
	}

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

	protected String getExecutionModulesPath() {
		return SlcJcrConstants.VM_AGENT_FACTORY_PATH + '/'
				+ agent.getAgentUuid() + '/' + SlcNames.SLC_EXECUTION_MODULES;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public void setAgent(SlcAgent agent) {
		this.agent = agent;
	}

}
