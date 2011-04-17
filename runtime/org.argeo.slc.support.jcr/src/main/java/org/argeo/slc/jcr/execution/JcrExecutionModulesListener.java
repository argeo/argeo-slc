package org.argeo.slc.jcr.execution;

import java.util.Arrays;
import java.util.Iterator;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.deploy.Module;
import org.argeo.slc.execution.ExecutionContext;
import org.argeo.slc.execution.ExecutionFlow;
import org.argeo.slc.execution.ExecutionModulesListener;

/**
 * Synchronizes the execution runtime with JCR. For the time being the state is
 * completely reset from one start to another.
 */
public class JcrExecutionModulesListener implements ExecutionModulesListener {
	private final static Log log = LogFactory
			.getLog(JcrExecutionModulesListener.class);

	private String modulesPath = "/slc/modules";

	private Session session;

	public void init() {
		try {
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

	public void executionModuleAdded(Module module,
			ExecutionContext executionContext) {
		try {
			Node base = session.getNode(modulesPath);
			Node moduleName = base.hasNode(module.getName()) ? base
					.getNode(module.getName()) : base.addNode(module.getName());
			Node moduleVersion = moduleName.hasNode(module.getVersion()) ? moduleName
					.getNode(module.getVersion()) : moduleName.addNode(module
					.getVersion());
			session.save();
		} catch (RepositoryException e) {
			throw new SlcException("Cannot add module " + module, e);
		}

	}

	public void executionModuleRemoved(Module module,
			ExecutionContext executionContext) {
		try {
			Node base = session.getNode(modulesPath);
			if (base.hasNode(module.getName())) {
				Node moduleName = base.getNode(module.getName());
				if (moduleName.hasNode(module.getVersion()))
					moduleName.getNode(module.getVersion()).remove();
				if (!moduleName.hasNodes())
					moduleName.remove();
				session.save();
			}
		} catch (RepositoryException e) {
			throw new SlcException("Cannot remove module " + module, e);
		}
	}

	public void executionFlowAdded(Module module, ExecutionFlow executionFlow) {
		String path = getExecutionFlowPath(module, executionFlow);
		log.debug("path=" + path);
		try {
			Node flowNode;
			if (!session.nodeExists(path)) {
				Node base = session.getNode(modulesPath);
				Node moduleNode = base.getNode(module.getName() + '/'
						+ module.getVersion());
				String relativePath = getExecutionFlowRelativePath(executionFlow);
				log.debug("relativePath='" + relativePath + "'");
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
				session.save();
			} else {
				flowNode = session.getNode(path);
			}
		} catch (RepositoryException e) {
			throw new SlcException("Cannot add flow " + executionFlow
					+ " from module " + module, e);
		}

	}

	public void executionFlowRemoved(Module module, ExecutionFlow executionFlow) {
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

	protected String getExecutionFlowPath(Module module,
			ExecutionFlow executionFlow) {
		String relativePath = getExecutionFlowRelativePath(executionFlow);
		return modulesPath + '/' + module.getName() + '/' + module.getVersion()
				+ '/' + relativePath;
	}

	/** @return the relative path, never starts with '/' */
	@SuppressWarnings("deprecation")
	protected String getExecutionFlowRelativePath(ExecutionFlow executionFlow) {
		String relativePath = executionFlow.getPath() == null ? executionFlow
				.getName() : executionFlow.getPath() + '/'
				+ executionFlow.getName();
		// we assume that it is more than one char long
		if (relativePath.charAt(0) == '/')
			relativePath = relativePath.substring(1);
		return relativePath;
	}

	public void setSession(Session session) {
		this.session = session;
	}

}
