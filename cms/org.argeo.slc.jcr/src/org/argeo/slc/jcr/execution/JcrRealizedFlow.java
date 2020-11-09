package org.argeo.slc.jcr.execution;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.SlcNames;
import org.argeo.slc.SlcTypes;
import org.argeo.slc.execution.ExecutionFlowDescriptor;
import org.argeo.slc.execution.ExecutionSpecAttribute;
import org.argeo.slc.execution.RealizedFlow;
import org.argeo.slc.execution.RefSpecAttribute;
import org.argeo.slc.jcr.SlcJcrUtils;
import org.argeo.slc.primitive.PrimitiveSpecAttribute;
import org.argeo.slc.primitive.PrimitiveUtils;
import org.argeo.slc.runtime.DefaultExecutionSpec;

public class JcrRealizedFlow extends RealizedFlow implements SlcNames {
	private static final long serialVersionUID = -3709453850260712001L;
	private String path;

	public JcrRealizedFlow(Node node) {
		try {
			this.path = node.getPath();
			loadFromNode(node);
		} catch (RepositoryException e) {
			throw new SlcException("Cannot initialize from " + node, e);
		}
	}

	protected void loadFromNode(Node realizedFlowNode) throws RepositoryException {
		if (realizedFlowNode.hasNode(SLC_ADDRESS)) {
			String flowPath = realizedFlowNode.getNode(SLC_ADDRESS).getProperty(Property.JCR_PATH).getString();
			// TODO: convert to local path if remote
			// FIXME start related module
			Session agentSession = realizedFlowNode.getSession().getRepository().login();
			try {
				Node flowNode = agentSession.getNode(flowPath);
				String flowName = flowNode.getProperty(SLC_NAME).getString();
				String description = null;
				if (flowNode.hasProperty(Property.JCR_DESCRIPTION))
					description = flowNode.getProperty(Property.JCR_DESCRIPTION).getString();

				Node executionModuleNode = flowNode.getSession().getNode(SlcJcrUtils.modulePath(flowPath));
				String executionModuleName = executionModuleNode.getProperty(SLC_NAME).getString();
				String executionModuleVersion = executionModuleNode.getProperty(SLC_VERSION).getString();

				RealizedFlow realizedFlow = this;
				realizedFlow.setModuleName(executionModuleName);
				realizedFlow.setModuleVersion(executionModuleVersion);

				// retrieve execution spec
				DefaultExecutionSpec executionSpec = new DefaultExecutionSpec();
				Map<String, ExecutionSpecAttribute> attrs = readExecutionSpecAttributes(realizedFlowNode);
				executionSpec.setAttributes(attrs);

				// set execution spec name
				if (flowNode.hasProperty(SlcNames.SLC_SPEC)) {
					Node executionSpecNode = flowNode.getProperty(SLC_SPEC).getNode();
					executionSpec.setName(executionSpecNode.getProperty(SLC_NAME).getString());
				}

				// explicitly retrieve values
				Map<String, Object> values = new HashMap<String, Object>();
				for (String attrName : attrs.keySet()) {
					ExecutionSpecAttribute attr = attrs.get(attrName);
					Object value = attr.getValue();
					values.put(attrName, value);
				}

				ExecutionFlowDescriptor efd = new ExecutionFlowDescriptor(flowName, description, values, executionSpec);
				realizedFlow.setFlowDescriptor(efd);

			} finally {
				JcrUtils.logoutQuietly(agentSession);
			}
		} else {
			throw new SlcException("Unsupported realized flow " + realizedFlowNode);
		}
	}

	protected Map<String, ExecutionSpecAttribute> readExecutionSpecAttributes(Node node) {
		try {
			Map<String, ExecutionSpecAttribute> attrs = new HashMap<String, ExecutionSpecAttribute>();
			for (NodeIterator nit = node.getNodes(); nit.hasNext();) {
				Node specAttrNode = nit.nextNode();
				if (specAttrNode.isNodeType(SlcTypes.SLC_PRIMITIVE_SPEC_ATTRIBUTE)) {
					String type = specAttrNode.getProperty(SLC_TYPE).getString();
					Object value = null;
					if (specAttrNode.hasProperty(SLC_VALUE)) {
						String valueStr = specAttrNode.getProperty(SLC_VALUE).getString();
						value = PrimitiveUtils.convert(type, valueStr);
					}
					PrimitiveSpecAttribute specAttr = new PrimitiveSpecAttribute(type, value);
					attrs.put(specAttrNode.getName(), specAttr);
				} else if (specAttrNode.isNodeType(SlcTypes.SLC_REF_SPEC_ATTRIBUTE)) {
					if (!specAttrNode.hasProperty(SLC_VALUE)) {
						continue;
					}
					Integer value = (int) specAttrNode.getProperty(SLC_VALUE).getLong();
					RefSpecAttribute specAttr = new RefSpecAttribute();
					NodeIterator children = specAttrNode.getNodes();
					int index = 0;
					String id = null;
					while (children.hasNext()) {
						Node child = children.nextNode();
						if (index == value)
							id = child.getName();
						index++;
					}
					specAttr.setValue(id);
					attrs.put(specAttrNode.getName(), specAttr);
				}
				// throw new SlcException("Unsupported spec attribute "
				// + specAttrNode);
			}
			return attrs;
		} catch (RepositoryException e) {
			throw new SlcException("Cannot read spec attributes from " + node, e);
		}
	}

	public String getPath() {
		return path;
	}
}
