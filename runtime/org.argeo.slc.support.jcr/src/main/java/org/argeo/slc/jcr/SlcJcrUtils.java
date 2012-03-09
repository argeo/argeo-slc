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
package org.argeo.slc.jcr;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.core.execution.PrimitiveAccessor;
import org.argeo.slc.core.execution.PrimitiveUtils;
import org.argeo.slc.deploy.ModuleDescriptor;
import org.argeo.slc.test.TestStatus;

/**
 * Utilities around the SLC JCR model. Note that it relies on fixed base paths
 * (convention over configuration) for optimization purposes.
 */
public class SlcJcrUtils implements SlcNames {
	public final static Integer AGENT_FACTORY_DEPTH = 3;

	/** Extracts the path of a flow relative to its execution module */
	public static String flowRelativePath(String fullFlowPath) {
		String[] tokens = fullFlowPath.split("/");
		StringBuffer buf = new StringBuffer(fullFlowPath.length());
		for (int i = AGENT_FACTORY_DEPTH + 3; i < tokens.length; i++) {
			buf.append('/').append(tokens[i]);
		}
		return buf.toString();
	}

	/** Module node name based on module name and version */
	public static String getModuleNodeName(ModuleDescriptor moduleDescriptor) {
		return moduleDescriptor.getName() + "_" + moduleDescriptor.getVersion();
	}

	/** Extracts the execution module name of a flow */
	public static String flowExecutionModuleName(String fullFlowPath) {
		String[] tokens = fullFlowPath.split("/");
		String moduleNodeName = tokens[AGENT_FACTORY_DEPTH + 2];
		return moduleNodeName.substring(0, moduleNodeName.lastIndexOf('_'));
	}

	/** Extracts the execution module version of a flow */
	public static String flowExecutionModuleVersion(String fullFlowPath) {
		String[] tokens = fullFlowPath.split("/");
		String moduleNodeName = tokens[AGENT_FACTORY_DEPTH + 2];
		return moduleNodeName.substring(moduleNodeName.lastIndexOf('_') + 1);
	}

	/** Extracts the agent factory of a flow */
	public static String flowAgentFactoryPath(String fullFlowPath) {
		String[] tokens = fullFlowPath.split("/");
		StringBuffer buf = new StringBuffer(fullFlowPath.length());
		// first token is always empty
		for (int i = 1; i < AGENT_FACTORY_DEPTH + 1; i++) {
			buf.append('/').append(tokens[i]);
		}
		return buf.toString();
	}

	/** Create a new execution process path based on the current time */
	public static String createExecutionProcessPath(String uuid) {
		Calendar now = new GregorianCalendar();
		return SlcJcrConstants.PROCESSES_BASE_PATH + '/'
				+ JcrUtils.dateAsPath(now, true) + uuid;
	}

	/** Create a new execution result path based on the current time */
	public static String createResultPath(String uuid) {
		Calendar now = new GregorianCalendar();
		return SlcJcrConstants.RESULTS_BASE_PATH + '/'
				+ JcrUtils.dateAsPath(now, true) + uuid;
	}

	/**
	 * Set the value of the primitive accessor as a JCR property. Does nothing
	 * if the value is null.
	 */
	public static void setPrimitiveAsProperty(Node node, String propertyName,
			PrimitiveAccessor primitiveAccessor) {
		String type = primitiveAccessor.getType();
		Object value = primitiveAccessor.getValue();
		setPrimitiveAsProperty(node, propertyName, type, value);
	}

	/** Map a primitive value to JCR property value. */
	public static void setPrimitiveAsProperty(Node node, String propertyName,
			String type, Object value) {
		if (value == null)
			return;
		if (value instanceof CharSequence)
			value = PrimitiveUtils.convert(type,
					((CharSequence) value).toString());

		try {
			if (type.equals(PrimitiveAccessor.TYPE_STRING))
				node.setProperty(propertyName, value.toString());
			else if (type.equals(PrimitiveAccessor.TYPE_INTEGER))
				node.setProperty(propertyName, (long) ((Integer) value));
			else if (type.equals(PrimitiveAccessor.TYPE_LONG))
				node.setProperty(propertyName, ((Long) value));
			else if (type.equals(PrimitiveAccessor.TYPE_FLOAT))
				node.setProperty(propertyName, (double) ((Float) value));
			else if (type.equals(PrimitiveAccessor.TYPE_DOUBLE))
				node.setProperty(propertyName, ((Double) value));
			else if (type.equals(PrimitiveAccessor.TYPE_BOOLEAN))
				node.setProperty(propertyName, ((Boolean) value));
			else
				throw new SlcException("Unsupported type " + type);
		} catch (RepositoryException e) {
			throw new SlcException("Cannot set primitive of " + type
					+ " as property " + propertyName + " on " + node, e);
		}
	}

	/** Aggregates the {@link TestStatus} of this sub-tree. */
	public static Integer aggregateTestStatus(Node node) {
		try {
			Integer status = TestStatus.PASSED;
			if (node.isNodeType(SlcTypes.SLC_CHECK))
				if (node.getProperty(SLC_SUCCESS).getBoolean())
					status = TestStatus.PASSED;
				else if (node.hasProperty(SLC_ERROR_MESSAGE))
					status = TestStatus.ERROR;
				else
					status = TestStatus.FAILED;

			NodeIterator it = node.getNodes();
			while (it.hasNext()) {
				Integer childStatus = aggregateTestStatus(it.nextNode());
				if (childStatus > status)
					status = childStatus;
			}
			return status;
		} catch (Exception e) {
			throw new SlcException("Could not aggregate test status from "
					+ node, e);
		}
	}

	/**
	 * Aggregates the {@link TestStatus} of this sub-tree.
	 * 
	 * @return the same {@link StringBuffer}, for convenience (typically calling
	 *         toString() on it)
	 */
	public static StringBuffer aggregateTestMessages(Node node,
			StringBuffer messages) {
		try {
			if (node.isNodeType(SlcTypes.SLC_CHECK)) {
				if (node.hasProperty(SLC_MESSAGE)) {
					if (messages.length() > 0)
						messages.append('\n');
					messages.append(node.getProperty(SLC_MESSAGE).getString());
				}
				if (node.hasProperty(SLC_ERROR_MESSAGE)) {
					if (messages.length() > 0)
						messages.append('\n');
					messages.append(node.getProperty(SLC_ERROR_MESSAGE)
							.getString());
				}
			}
			NodeIterator it = node.getNodes();
			while (it.hasNext()) {
				Node child = it.nextNode();
				aggregateTestMessages(child, messages);
			}
			return messages;
		} catch (Exception e) {
			throw new SlcException("Could not aggregate test messages from "
					+ node, e);
		}
	}
	
	/** Prevents instantiation */
	private SlcJcrUtils() {

	}

	public static void main(String[] args) {
		String path = "/slc/agents/vm/default/org.argeo_1.2.3/myPath/myFlow";
		System.out.println("Flow relative path: " + flowRelativePath(path));
		System.out.println("Execution Module Name: "
				+ flowExecutionModuleName(path));
		System.out.println("Execution Module Version: "
				+ flowExecutionModuleVersion(path));
		System.out.println("Agent Factory path: " + flowAgentFactoryPath(path));
	}

}
