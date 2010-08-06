package org.argeo.slc.client.ui.views;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.eclipse.ui.TreeObject;
import org.argeo.eclipse.ui.TreeParent;
import org.argeo.slc.execution.ExecutionFlowDescriptor;
import org.argeo.slc.execution.ExecutionModuleDescriptor;
import org.argeo.slc.runtime.SlcAgent;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ExecutionModulesContentProvider implements ITreeContentProvider {
	private final static Log log = LogFactory
			.getLog(ExecutionModulesContentProvider.class);

	private SlcAgent slcAgent;

	public Object[] getChildren(Object parent) {
		if (parent instanceof ExecutionModuleNode) {
			ExecutionModuleNode executionModuleNode = (ExecutionModuleNode) parent;
			ExecutionModuleDescriptor emd = executionModuleNode.getDescriptor();
			emd = executionModuleNode.getAgentNode().getAgent()
					.getExecutionModuleDescriptor(emd.getName(),
							emd.getVersion());
			executionModuleNode.cacheDescriptor(emd);
			// for (String flowName : executionModuleNode.getFlowDescriptors()
			// .keySet()) {
			// executionModuleNode.addChild(new FlowNode(flowName,
			// executionModuleNode));
			// }
			return executionModuleNode.getChildren();
		} else if (parent instanceof AgentNode) {
			AgentNode agentNode = (AgentNode) parent;

			if (log.isTraceEnabled())
				log.trace("Scan agent " + agentNode);

			agentNode.clearChildren();
			for (ExecutionModuleDescriptor desc : agentNode.getAgent()
					.listExecutionModuleDescriptors()) {
				agentNode.addChild(new ExecutionModuleNode(agentNode, desc));
			}

			return agentNode.getChildren();
		} else if (parent instanceof TreeParent) {
			return ((TreeParent) parent).getChildren();
		} else if (parent instanceof FlowNode) {
			return new Object[0];
		} else {
			log.trace(parent);
			Object[] arr = { new AgentNode(slcAgent) };
			return arr;
		}
	}

	public Object getParent(Object node) {
		// if (node instanceof TreeObject) {
		// return ((TreeObject) node).getParent();
		// }
		return null;
	}

	public boolean hasChildren(Object parent) {
		if (parent instanceof TreeParent && ((TreeParent) parent).isLoaded()) {
			return ((TreeParent) parent).hasChildren();
		} else if (parent instanceof AgentNode) {
			return true;
		} else if (parent instanceof ExecutionModuleNode) {
			return true;
		}
		return false;
	}

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}

	public void dispose() {
	}

	public Object[] getElements(Object parent) {
		return getChildren(parent);
	}

	public void setSlcAgent(SlcAgent slcAgent) {
		this.slcAgent = slcAgent;
	}

	public class AgentNode extends TreeParent {
		private final SlcAgent agent;

		public AgentNode(SlcAgent agent) {
			super(agent.toString());
			this.agent = agent;
		}

		public SlcAgent getAgent() {
			return agent;
		}
	}

	public class ExecutionModuleNode extends TreeParent {
		private final AgentNode agentNode;
		private ExecutionModuleDescriptor descriptor;
		private Map<String, ExecutionFlowDescriptor> flowDescriptors;

		public ExecutionModuleNode(AgentNode agentNode,
				ExecutionModuleDescriptor descriptor) {
			super(descriptor.toString());
			this.agentNode = agentNode;
			this.descriptor = descriptor;

		}

		public AgentNode getAgentNode() {
			return agentNode;
		}

		public ExecutionModuleDescriptor getDescriptor() {
			return descriptor;
		}

		public void cacheDescriptor(ExecutionModuleDescriptor descriptor) {
			this.descriptor = descriptor;

			SortedMap<String, FolderNode> folderNodes = new TreeMap<String, FolderNode>();

			// SortedMap<String, FlowNode> directChildren = new TreeMap<String,
			// FlowNode>();

			flowDescriptors = new HashMap<String, ExecutionFlowDescriptor>();
			for (ExecutionFlowDescriptor fd : descriptor.getExecutionFlows()) {
				if (log.isTraceEnabled())
					log
							.trace("path=" + fd.getPath() + ", name="
									+ fd.getName());

				String path = fd.getPath();
				String name = fd.getName();

				if (path == null || path.trim().equals("")) {
					// directChildren.put(name, new FlowNode(name, this));
					addChild(new FlowNode(name, this));
				} else {
					FolderNode folderNode = mkdirs(this, path, folderNodes);
					folderNode.addChild(new FlowNode(name, this));
				}

				flowDescriptors.put(fd.getName(), fd);
			}
			// TODO: make it readonly
		}

		protected FolderNode mkdirs(TreeParent root, String path,
				SortedMap<String, FolderNode> folderNodes) {
			if (path.charAt(path.length() - 1) == '/')
				path = path.substring(0, path.length() - 1);

			if (folderNodes.containsKey(path))
				return folderNodes.get(path);

			int lastIndx = path.lastIndexOf('/');
			String folderName = path.substring(lastIndx + 1);
			String parentPath = path.substring(0, lastIndx);

			TreeParent parent;
			if (parentPath.equals(""))
				parent = root;
			else
				parent = mkdirs(root, parentPath, folderNodes);
			FolderNode newFolder = new FolderNode(folderName);
			parent.addChild(newFolder);
			folderNodes.put(path, newFolder);
			return newFolder;
		}

		public Map<String, ExecutionFlowDescriptor> getFlowDescriptors() {
			return flowDescriptors;
		}

	}

	public class FlowNode extends TreeObject {
		private final String flowName;
		private final ExecutionModuleNode executionModuleNode;

		public FlowNode(String flowName, ExecutionModuleNode executionModuleNode) {
			super(flowName);
			this.flowName = flowName;
			this.executionModuleNode = executionModuleNode;
		}

		public String getFlowName() {
			return flowName;
		}

		public ExecutionModuleNode getExecutionModuleNode() {
			return executionModuleNode;
		}

	}

	public class FolderNode extends TreeParent {
		public FolderNode(String name) {
			super(name);
		}

	}
}
