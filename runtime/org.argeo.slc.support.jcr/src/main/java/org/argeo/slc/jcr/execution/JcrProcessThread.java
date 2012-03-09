/*
 * Copyright (C) 2007-2012 Mathieu Baudier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import org.argeo.slc.core.execution.RefSpecAttribute;
import org.argeo.slc.execution.ExecutionFlowDescriptor;
import org.argeo.slc.execution.ExecutionModulesManager;
import org.argeo.slc.execution.ExecutionProcess;
import org.argeo.slc.execution.ExecutionSpecAttribute;
import org.argeo.slc.jcr.SlcJcrUtils;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.argeo.slc.process.RealizedFlow;

/** Where the actual execution takes place */
public class JcrProcessThread extends ProcessThread implements SlcNames {

	public JcrProcessThread(ThreadGroup processesThreadGroup,
			ExecutionModulesManager executionModulesManager,
			JcrExecutionProcess process) {
		super(processesThreadGroup, executionModulesManager, process);
	}

	@Override
	protected void process() throws InterruptedException {
		try {
			Node rootRealizedFlowNode = getNode().getNode(SLC_FLOW);
			// we just manage one level for the time being
			NodeIterator nit = rootRealizedFlowNode.getNodes(SLC_FLOW);
			while (nit.hasNext()) {
				Node realizedFlowNode = nit.nextNode();

				// set status on realized flow
				realizedFlowNode.setProperty(SLC_STATUS,
						ExecutionProcess.RUNNING);
				realizedFlowNode.getSession().save();
				try {
					execute(realizedFlowNode);

					// set status on realized flow
					realizedFlowNode.setProperty(SLC_STATUS,
							ExecutionProcess.COMPLETED);
					realizedFlowNode.getSession().save();
				} catch (RepositoryException e) {
					throw e;
				} catch (InterruptedException e) {
					// set status on realized flow
					realizedFlowNode.setProperty(SLC_STATUS,
							ExecutionProcess.KILLED);
					realizedFlowNode.getSession().save();
					throw e;
				} catch (RuntimeException e) {
					// set status on realized flow
					realizedFlowNode.setProperty(SLC_STATUS,
							ExecutionProcess.ERROR);
					realizedFlowNode.getSession().save();
					throw e;
				}
			}
		} catch (RepositoryException e) {
			throw new ArgeoException("Cannot process " + getNode(), e);
		}
	}

	/** Configure the realized flows */
	protected void execute(Node realizedFlowNode) throws RepositoryException,
			InterruptedException {
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

			// retrieve execution spec
			DefaultExecutionSpec executionSpec = new DefaultExecutionSpec();
			Map<String, ExecutionSpecAttribute> attrs = readExecutionSpecAttributes(realizedFlowNode);
			executionSpec.setAttributes(attrs);

			// set execution spec name
			if (flowNode.hasProperty(SlcNames.SLC_SPEC)) {
				Node executionSpecNode = flowNode.getProperty(SLC_SPEC)
						.getNode();
				executionSpec.setBeanName(executionSpecNode.getProperty(
						SLC_NAME).getString());
			}

			// explicitly retrieve values
			Map<String, Object> values = new HashMap<String, Object>();
			for (String attrName : attrs.keySet()) {
				ExecutionSpecAttribute attr = attrs.get(attrName);
				Object value = attr.getValue();
				values.put(attrName, value);
			}

			ExecutionFlowDescriptor efd = new ExecutionFlowDescriptor(flowName,
					values, executionSpec);
			realizedFlow.setFlowDescriptor(efd);

			//
			// EXECUTE THE FLOW
			//
			execute(realizedFlow, true);
			//
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
					Object value = null;
					if (specAttrNode.hasProperty(SLC_VALUE)) {
						String valueStr = specAttrNode.getProperty(SLC_VALUE)
								.getString();
						value = PrimitiveUtils.convert(type, valueStr);
					}
					PrimitiveSpecAttribute specAttr = new PrimitiveSpecAttribute(
							type, value);
					attrs.put(specAttrNode.getName(), specAttr);
				} else if (specAttrNode
						.isNodeType(SlcTypes.SLC_REF_SPEC_ATTRIBUTE)) {
					if (!specAttrNode.hasProperty(SLC_VALUE)) {
						continue;
					}
					Integer value = (int) specAttrNode.getProperty(SLC_VALUE)
							.getLong();
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
			throw new SlcException("Cannot read spec attributes from " + node,
					e);
		}
	}

	protected Node getNode() {
		return ((JcrExecutionProcess) getProcess()).getNode();
	}
}
