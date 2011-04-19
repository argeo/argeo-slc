package org.argeo.slc.jcr.execution;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

import org.argeo.ArgeoException;
import org.argeo.slc.core.execution.ProcessThread;
import org.argeo.slc.execution.ExecutionFlowDescriptor;
import org.argeo.slc.execution.ExecutionModulesManager;
import org.argeo.slc.jcr.SlcJcrUtils;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.process.RealizedFlow;

public class JcrProcessThread extends ProcessThread implements SlcNames {

	public JcrProcessThread(ExecutionModulesManager executionModulesManager,
			JcrExecutionProcess process) {
		super(executionModulesManager, process);
	}

	@Override
	protected void process() {
		try {
			Node realizedFlowNode = getNode().getNode(SLC_FLOW);
			// we just manage one level for the time being
			NodeIterator nit = realizedFlowNode.getNodes(SLC_FLOW);
			while (nit.hasNext()) {
				process(nit.nextNode());
			}
		} catch (RepositoryException e) {
			throw new ArgeoException("Cannot process " + getNode(), e);
		}
	}

	protected void process(Node realizedFlowNode) throws RepositoryException {
		if (realizedFlowNode.hasNode(SLC_ADDRESS)) {
			String flowPath = realizedFlowNode.getNode(SLC_ADDRESS)
					.getProperty(Property.JCR_PATH).getString();
			// TODO: convert to local path if remote

			Node flowNode = realizedFlowNode.getSession().getNode(flowPath);
			String flowName = flowNode.getProperty(SLC_NAME).getString();

			String executionModuleName = SlcJcrUtils
					.flowExecutionModuleName(flowPath);
			String executionModuleVersion = SlcJcrUtils
					.flowExecutionModuleVersion(flowPath);

			RealizedFlow realizedFlow = new RealizedFlow();
			realizedFlow.setModuleName(executionModuleName);
			realizedFlow.setModuleVersion(executionModuleVersion);

			ExecutionFlowDescriptor efd = new ExecutionFlowDescriptor(flowName,
					null, null);
			realizedFlow.setFlowDescriptor(efd);

			execute(realizedFlow, true);
		}
	}

	protected Node getNode() {
		return ((JcrExecutionProcess) getProcess()).getNode();
	}
}
