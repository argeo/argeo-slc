package org.argeo.slc.client.ui.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

	private List<SlcAgent> slcAgents;

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
			List<AgentNode> agentNodes = new ArrayList<AgentNode>();
			for (SlcAgent slcAgent : slcAgents) {
				agentNodes.add(new AgentNode(slcAgent));
			}
			return agentNodes.toArray();
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

	public void setSlcAgents(List<SlcAgent> slcAgents) {
		this.slcAgents = slcAgents;
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

			flowDescriptors = new HashMap<String, ExecutionFlowDescriptor>();
			for (ExecutionFlowDescriptor fd : descriptor.getExecutionFlows()) {
				// if (log.isTraceEnabled())
				// log.trace("fd.path=" + fd.getPath() + ", fd.name="
				// + fd.getName());

				// find path and label
				String path;
				String label;
				int lastSlash = fd.getName().lastIndexOf('/');
				if ((fd.getPath() == null || fd.getPath().trim().equals(""))
						&& lastSlash >= 0) {
					path = fd.getName().substring(0, lastSlash);
					label = fd.getName().substring(lastSlash + 1);
				} else {
					path = fd.getPath();
					label = fd.getName();
				}
				// if (log.isTraceEnabled())
				// log.trace("path=" + path + ", label=" + label);

				if (path == null || path.trim().equals("")
						|| path.trim().equals("/")) {
					// directChildren.put(name, new FlowNode(name, this));
					addChild(new FlowNode(label, fd.getName(), this));
				} else {
					FolderNode folderNode = mkdirs(this, path, folderNodes);
					folderNode
							.addChild(new FlowNode(label, fd.getName(), this));
				}

				flowDescriptors.put(fd.getName(), fd);
			}
			// TODO: make it readonly
		}

		protected FolderNode mkdirs(TreeParent root, String path,
				SortedMap<String, FolderNode> folderNodes) {
			// Normalize
			if (path.charAt(0) != '/')
				path = '/' + path;
			if (path.charAt(path.length() - 1) == '/')
				path = path.substring(0, path.length() - 1);

			if (folderNodes.containsKey(path))
				return folderNodes.get(path);

			int lastIndx = path.lastIndexOf('/');
			String folderName;
			String parentPath;
			if (lastIndx >= 0) {
				folderName = path.substring(lastIndx + 1);
				parentPath = path.substring(0, lastIndx);
			} else {
				folderName = path;
				parentPath = "";
			}

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

		public FlowNode(String label, String flowName,
				ExecutionModuleNode executionModuleNode) {
			super(label);
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
