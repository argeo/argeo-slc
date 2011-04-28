package org.argeo.slc.jcr.execution;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

import org.argeo.ArgeoException;
import org.argeo.slc.SlcException;
import org.argeo.slc.core.execution.DefaultExecutionSpec;
import org.argeo.slc.core.execution.PrimitiveSpecAttribute;
import org.argeo.slc.core.execution.PrimitiveUtils;
import org.argeo.slc.core.execution.ProcessThread;
import org.argeo.slc.execution.ExecutionFlowDescriptor;
import org.argeo.slc.execution.ExecutionModulesManager;
import org.argeo.slc.execution.ExecutionSpecAttribute;
import org.argeo.slc.jcr.SlcJcrUtils;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.argeo.slc.process.RealizedFlow;

/** Where the actual execution takes place */
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

	/** Configure the realized flows */
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

			DefaultExecutionSpec executionSpec = null;
			if (flowNode.hasProperty(SlcNames.SLC_SPEC)) {
				Node executionSpecNode = flowNode.getProperty(SLC_SPEC)
						.getNode();
				executionSpec = new DefaultExecutionSpec();
				executionSpec.setBeanName(executionSpecNode.getProperty(
						SLC_NAME).getString());
				executionSpec
						.setAttributes(readExecutionSpecAttributes(executionSpecNode));
			}
			// TODO: will with original attr
			Map<String, ExecutionSpecAttribute> attrs = readExecutionSpecAttributes(realizedFlowNode);
			Map<String, Object> values = new HashMap<String, Object>();
			for (String attrName : attrs.keySet()) {
				ExecutionSpecAttribute attr = attrs.get(attrName);
				Object value = attr.getValue();
				values.put(attrName,value);
			}
			
//			if(executionSpec!=null)
//				executionSpec.setAttributes(attrs);
			ExecutionFlowDescriptor efd = new ExecutionFlowDescriptor(flowName,
					values, executionSpec);
			realizedFlow.setFlowDescriptor(efd);

			execute(realizedFlow, true);
		}
	}

	protected Map<String, ExecutionSpecAttribute> readExecutionSpecAttributes(
			Node node) {
		try {
			Map<String, ExecutionSpecAttribute> attrs = new HashMap<String, ExecutionSpecAttribute>();
			for (NodeIterator nit = node.getNodes(); nit.hasNext();) {
				Node specAttrNode = nit.nextNode();
				if (specAttrNode
						.isNodeType(SlcTypes.SLC_PRIMITIVE_SPEC_ATTRIBUTE)) {
					String type = specAttrNode.getProperty(SLC_TYPE)
							.getString();
					if (specAttrNode.hasProperty(SLC_VALUE)) {
						String valueStr = specAttrNode.getProperty(SLC_VALUE)
								.getString();
						Object value = PrimitiveUtils.convert(type, valueStr);
						PrimitiveSpecAttribute specAttr = new PrimitiveSpecAttribute(
								type, value);
						attrs.put(specAttrNode.getName(), specAttr);
					}
				}

			}
			return attrs;
		} catch (RepositoryException e) {
			throw new SlcException("Cannot read spec attributes from " + node,
					e);
		}
	}

	protected Node getNode() {
		return ((JcrExecutionProcess) getProcess()).getNode();
	}
}
