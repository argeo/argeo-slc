package org.argeo.slc.client.contentprovider;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.process.RealizedFlow;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.process.SlcExecutionStep;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Basic tree view of the chosen process details. For now, only a basic list of
 * details is displayed, not a tree.
 */
public class ProcessDetailContentProvider implements ITreeContentProvider {
	private final static Log log = LogFactory
			.getLog(ProcessDetailContentProvider.class);

	private SlcExecution slcExecution;

	// private List<SlcAgent> slcAgents;

	public Object[] getChildren(Object parent) {
		if (parent instanceof SlcExecution) {
			slcExecution = (SlcExecution) parent;

			List<SlcExecutionStep> steps = slcExecution.getSteps();
			List<RealizedFlow> realizedFlows = slcExecution.getRealizedFlows();

			for (int i = 0; i < steps.size(); i++) {
				log.debug("step[" + i + "] : " + steps.get(i).getType());
			}
			for (int i = 0; i < realizedFlows.size(); i++) {
				log.debug("step[" + i + "] : "
						+ realizedFlows.get(i).toString());
			}

			log.debug(" Realized flows : ");
			return steps.toArray();
		}
		// if (parent instanceof ExecutionModuleNode) {
		// ExecutionModuleNode executionModuleNode = (ExecutionModuleNode)
		// parent;
		// ExecutionModuleDescriptor emd =
		// executionModuleNode.getDescriptor();
		// emd = executionModuleNode.getAgentNode().getAgent()
		// .getExecutionModuleDescriptor(emd.getName(),
		// emd.getVersion());
		// executionModuleNode.cacheDescriptor(emd);
		// // for (String flowName :
		// executionModuleNode.getFlowDescriptors()
		// // .keySet()) {
		// // executionModuleNode.addChild(new FlowNode(flowName,
		// // executionModuleNode));
		// // }
		// return executionModuleNode.getChildren();
		// } else if (parent instanceof AgentNode) {
		// AgentNode agentNode = (AgentNode) parent;
		//
		// if (log.isTraceEnabled())
		// log.trace("Scan agent " + agentNode);
		//
		// agentNode.clearChildren();
		// for (ExecutionModuleDescriptor desc : agentNode.getAgent()
		// .listExecutionModuleDescriptors()) {
		// agentNode.addChild(new ExecutionModuleNode(agentNode, desc));
		// }
		//
		// return agentNode.getChildren();
		// } else if (parent instanceof TreeParent) {
		// return ((TreeParent) parent).getChildren();
		// } else if (parent instanceof FlowNode) {
		// return new Object[0];
		// } else {
		// List<AgentNode> agentNodes = new ArrayList<AgentNode>();
		// for (SlcAgent slcAgent : slcAgents) {
		// agentNodes.add(new AgentNode(slcAgent));
		// }
		// return agentNodes.toArray();
		// }
		return null;
	}

	public Object getParent(Object node) {
		// if (node instanceof TreeObject) {
		// return ((TreeObject) node).getParent();
		// }
		return null;
	}

	public boolean hasChildren(Object parent) {
		return false;
	}

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}

	public void dispose() {
	}

	public Object[] getElements(Object parent) {
		// return getChildren(parent);
		// Here we must duplicate the code otherwise the inner call to method
		// getChildren(parent); is not intercepted by AspectJ
		if (parent instanceof SlcExecution) {
			slcExecution = (SlcExecution) parent;

			List<SlcExecutionStep> steps = slcExecution.getSteps();
			List<RealizedFlow> realizedFlows = slcExecution.getRealizedFlows();

			for (int i = 0; i < steps.size(); i++) {
				log.debug("step[" + i + "] : " + steps.get(i).getType());
			}
			for (int i = 0; i < realizedFlows.size(); i++) {
				log.debug("step[" + i + "] : "
						+ realizedFlows.get(i).toString());
			}

			log.debug(" Realized flows : ");
			return steps.toArray();
		}
		return null;
	}
}
