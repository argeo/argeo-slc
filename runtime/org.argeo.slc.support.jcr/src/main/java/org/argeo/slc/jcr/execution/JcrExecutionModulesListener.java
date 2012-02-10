package org.argeo.slc.jcr.execution;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.core.execution.PrimitiveSpecAttribute;
import org.argeo.slc.core.execution.PrimitiveValue;
import org.argeo.slc.core.execution.RefSpecAttribute;
import org.argeo.slc.core.execution.RefValueChoice;
import org.argeo.slc.deploy.ModuleDescriptor;
import org.argeo.slc.execution.ExecutionFlowDescriptor;
import org.argeo.slc.execution.ExecutionModulesListener;
import org.argeo.slc.execution.ExecutionModulesManager;
import org.argeo.slc.execution.ExecutionSpec;
import org.argeo.slc.execution.ExecutionSpecAttribute;
import org.argeo.slc.jcr.SlcJcrUtils;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;

/**
 * Synchronizes the local execution runtime with a JCR repository. For the time
 * being the state is completely reset from one start to another.
 */
public class JcrExecutionModulesListener implements ExecutionModulesListener,
		SlcNames {
	private final static String SLC_EXECUTION_MODULES_PROPERTY = "slc.executionModules";

	private final static Log log = LogFactory
			.getLog(JcrExecutionModulesListener.class);
	private JcrAgent agent;

	private ExecutionModulesManager modulesManager;

	private Repository repository;
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
		try {
			session = repository.login();
			clearAgent();
			if (modulesManager != null) {
				List<ModuleDescriptor> moduleDescriptors = modulesManager
						.listModules();
				String executionModules = System
						.getProperty(SLC_EXECUTION_MODULES_PROPERTY);
				if (executionModules != null) {
					Node agentNode = session.getNode(agent.getNodePath());
					for (String executionModule : executionModules.split(",")) {
						allModules: for (ModuleDescriptor moduleDescriptor : moduleDescriptors) {
							String moduleNodeName = SlcJcrUtils
									.getModuleNodeName(moduleDescriptor);
							if (moduleDescriptor.getName().equals(
									executionModule)) {
								Node moduleNode = agentNode
										.hasNode(moduleNodeName) ? agentNode
										.getNode(moduleNodeName) : agentNode
										.addNode(moduleNodeName);
								moduleNode
										.addMixin(SlcTypes.SLC_EXECUTION_MODULE);
								moduleNode.setProperty(SLC_NAME,
										moduleDescriptor.getName());
								moduleNode.setProperty(SLC_VERSION,
										moduleDescriptor.getVersion());
								moduleNode.setProperty(Property.JCR_TITLE,
										moduleDescriptor.getTitle());
								moduleNode.setProperty(
										Property.JCR_DESCRIPTION,
										moduleDescriptor.getDescription());
								moduleNode.setProperty(SLC_STARTED, false);
								break allModules;
							}
						}
					}
					if (session.hasPendingChanges())
						session.save();
				}
			}
		} catch (RepositoryException e) {
			JcrUtils.discardQuietly(session);
			JcrUtils.logoutQuietly(session);
			throw new SlcException("Cannot initialize modules", e);
		}
	}

	public void destroy() {
		clearAgent();
		JcrUtils.logoutQuietly(session);
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
			moduleNode.setProperty(SLC_NAME, moduleDescriptor.getName());
			moduleNode.setProperty(SLC_VERSION, moduleDescriptor.getVersion());
			moduleNode.setProperty(Property.JCR_TITLE,
					moduleDescriptor.getTitle());
			moduleNode.setProperty(Property.JCR_DESCRIPTION,
					moduleDescriptor.getDescription());
			moduleNode.setProperty(SLC_STARTED, true);
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
			if (agentNode.hasNode(moduleName)) {
				Node moduleNode = agentNode.getNode(moduleName);
				for (NodeIterator nit = moduleNode.getNodes(); nit.hasNext();) {
					nit.nextNode().remove();
				}
				moduleNode.setProperty(SLC_STARTED, false);
			}
			session.save();
		} catch (RepositoryException e) {
			JcrUtils.discardQuietly(session);
			throw new SlcException("Cannot remove module " + moduleDescriptor,
					e);
		}
	}

	public synchronized void executionFlowAdded(ModuleDescriptor module,
			ExecutionFlowDescriptor efd) {
		try {
			Node agentNode = session.getNode(agent.getNodePath());
			Node moduleNode = agentNode.getNode(SlcJcrUtils
					.getModuleNodeName(module));
			String relativePath = getExecutionFlowRelativePath(efd);
			@SuppressWarnings("unused")
			Node flowNode = null;
			if (!moduleNode.hasNode(relativePath)) {
				flowNode = createExecutionFlowNode(moduleNode, relativePath,
						efd);
				session.save();
			} else {
				flowNode = moduleNode.getNode(relativePath);
			}

			if (log.isTraceEnabled())
				log.trace("Flow " + efd + " added to JCR");
		} catch (RepositoryException e) {
			JcrUtils.discardQuietly(session);
			throw new SlcException("Cannot add flow " + efd + " from module "
					+ module, e);
		}

	}

	protected Node createExecutionFlowNode(Node moduleNode,
			String relativePath, ExecutionFlowDescriptor efd)
			throws RepositoryException {
		Node flowNode = null;
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

		// name, description
		flowNode.setProperty(SLC_NAME, efd.getName());
		String[] tokens = relativePath.split("/");
		String endName = tokens[tokens.length - 1];
		flowNode.setProperty(Property.JCR_TITLE, endName);
		if (efd.getDescription() != null
				&& !efd.getDescription().trim().equals("")) {
			flowNode.setProperty(Property.JCR_DESCRIPTION, efd.getDescription());
		} else {
			flowNode.setProperty(Property.JCR_DESCRIPTION, endName);
		}

		// execution spec
		ExecutionSpec executionSpec = efd.getExecutionSpec();
		String esName = executionSpec.getName();
		if (!(esName == null || esName.equals(ExecutionSpec.INTERNAL_NAME))) {
			// reference spec node
			Node executionSpecsNode = moduleNode.hasNode(SLC_EXECUTION_SPECS) ? moduleNode
					.getNode(SLC_EXECUTION_SPECS) : moduleNode
					.addNode(SLC_EXECUTION_SPECS);
			Node executionSpecNode = executionSpecsNode.addNode(esName,
					SlcTypes.SLC_EXECUTION_SPEC);
			executionSpecNode.setProperty(SLC_NAME, esName);
			executionSpecNode.setProperty(Property.JCR_TITLE, esName);
			if (executionSpec.getDescription() != null
					&& !executionSpec.getDescription().trim().equals(""))
				executionSpecNode.setProperty(Property.JCR_DESCRIPTION,
						executionSpec.getDescription());
			mapExecutionSpec(executionSpecNode, executionSpec);
			flowNode.setProperty(SLC_SPEC, executionSpecNode);
		} else {
			// internal spec node
			mapExecutionSpec(flowNode, executionSpec);
		}

		// values
		for (String attr : efd.getValues().keySet()) {
			ExecutionSpecAttribute esa = executionSpec.getAttributes()
					.get(attr);
			if (esa instanceof PrimitiveSpecAttribute) {
				PrimitiveSpecAttribute psa = (PrimitiveSpecAttribute) esa;
				Node valueNode = flowNode.addNode(attr);
				valueNode.setProperty(SLC_TYPE, psa.getType());
				SlcJcrUtils.setPrimitiveAsProperty(valueNode, SLC_VALUE,
						(PrimitiveValue) efd.getValues().get(attr));
			}
		}

		return flowNode;
	}

	/**
	 * Base can be either an execution spec node, or an execution flow node (in
	 * case the execution spec is internal)
	 */
	protected void mapExecutionSpec(Node baseNode, ExecutionSpec executionSpec)
			throws RepositoryException {
		for (String attrName : executionSpec.getAttributes().keySet()) {
			ExecutionSpecAttribute esa = executionSpec.getAttributes().get(
					attrName);
			Node attrNode = baseNode.addNode(attrName);
			// booleans
			attrNode.addMixin(SlcTypes.SLC_EXECUTION_SPEC_ATTRIBUTE);
			attrNode.setProperty(SLC_IS_IMMUTABLE, esa.getIsImmutable());
			attrNode.setProperty(SLC_IS_CONSTANT, esa.getIsConstant());
			attrNode.setProperty(SLC_IS_HIDDEN, esa.getIsHidden());

			if (esa instanceof PrimitiveSpecAttribute) {
				attrNode.addMixin(SlcTypes.SLC_PRIMITIVE_SPEC_ATTRIBUTE);
				PrimitiveSpecAttribute psa = (PrimitiveSpecAttribute) esa;
				SlcJcrUtils.setPrimitiveAsProperty(attrNode, SLC_VALUE, psa);
				attrNode.setProperty(SLC_TYPE, psa.getType());
			} else if (esa instanceof RefSpecAttribute) {
				attrNode.addMixin(SlcTypes.SLC_REF_SPEC_ATTRIBUTE);
				RefSpecAttribute rsa = (RefSpecAttribute) esa;
				attrNode.setProperty(SLC_TYPE, rsa.getTargetClassName());
				if (rsa.getChoices() != null) {
					for (RefValueChoice choice : rsa.getChoices()) {
						Node choiceNode = attrNode.addNode(choice.getName());
						choiceNode.addMixin(NodeType.MIX_TITLE);
						choiceNode.setProperty(Property.JCR_TITLE,
								choice.getName());
						if (choice.getDescription() != null
								&& !choice.getDescription().trim().equals(""))
							choiceNode.setProperty(Property.JCR_DESCRIPTION,
									choice.getDescription());
					}
				}
			}
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
		// FIXME quick hack to avoid duplicate '/'
		relativePath = relativePath.replaceAll("//", "/");
		return relativePath;
	}

	/*
	 * BEAN
	 */
	public void setAgent(JcrAgent agent) {
		this.agent = agent;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public void setModulesManager(ExecutionModulesManager modulesManager) {
		this.modulesManager = modulesManager;
	}

}
