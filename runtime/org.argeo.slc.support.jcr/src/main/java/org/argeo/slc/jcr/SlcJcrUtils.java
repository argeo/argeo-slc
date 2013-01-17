/*
 * Copyright (C) 2007-2012 Argeo GmbH
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
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;

import org.argeo.jcr.JcrUtils;
import org.argeo.jcr.UserJcrUtils;
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

	/** Extracts the path to the related execution module */
	public static String modulePath(String fullFlowPath) {
		String[] tokens = fullFlowPath.split("/");
		StringBuffer buf = new StringBuffer(fullFlowPath.length());
		for (int i = 0; i < AGENT_FACTORY_DEPTH + 3; i++) {
			if (!tokens[i].equals(""))
				buf.append('/').append(tokens[i]);
		}
		return buf.toString();
	}

	/** Extracts the module name from a flow path */
	public static String moduleName(String fullFlowPath) {
		String[] tokens = fullFlowPath.split("/");
		String moduleName = tokens[AGENT_FACTORY_DEPTH + 2];
		moduleName = moduleName.substring(0, moduleName.indexOf('_'));
		return moduleName;
	}

	/** Module node name based on module name and version */
	public static String getModuleNodeName(ModuleDescriptor moduleDescriptor) {
		return moduleDescriptor.getName() + "_" + moduleDescriptor.getVersion();
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
	public static String createExecutionProcessPath(Session session, String uuid) {
		Calendar now = new GregorianCalendar();
		return getSlcProcessesBasePath(session) + '/'
				+ JcrUtils.dateAsPath(now, true) + uuid;
	}

	/** Get the base for the user processi. */
	public static String getSlcProcessesBasePath(Session session) {
		try {
			Node userHome = UserJcrUtils.getUserHome(session);
			if (userHome == null)
				throw new SlcException("No user home available for "
						+ session.getUserID());
			return userHome.getPath() + '/' + SlcNames.SLC_SYSTEM + '/'
					+ SlcNames.SLC_PROCESSES;
		} catch (RepositoryException re) {
			throw new SlcException(
					"Unexpected error while getting Slc Results Base Path.", re);
		}
	}

	/**
	 * Create a new execution result path in the user home based on the current
	 * time
	 */
	public static String createResultPath(Session session, String uuid)
			throws RepositoryException {
		Calendar now = new GregorianCalendar();
		StringBuffer absPath = new StringBuffer(
				SlcJcrResultUtils.getSlcResultsBasePath(session) + '/');
		// Remove hours and add title property to the result process path on
		// request of O. Capillon
		// return getSlcProcessesBasePath(session) + '/'
		// + JcrUtils.dateAsPath(now, true) + uuid;
		String relPath = JcrUtils.dateAsPath(now, false);
		List<String> names = JcrUtils.tokenize(relPath);
		for (String name : names) {
			absPath.append(name + "/");
			Node node = JcrUtils.mkdirs(session, absPath.toString());
			try {
				node.addMixin(NodeType.MIX_TITLE);
				node.setProperty(Property.JCR_TITLE, name.substring(1));
			} catch (RepositoryException e) {
				throw new SlcException(
						"unable to create execution process path", e);
			}
		}
		return absPath.toString() + uuid;
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
		if (value instanceof char[])
			value = new String((char[]) value);

		try {
			if (type.equals(PrimitiveAccessor.TYPE_STRING))
				node.setProperty(propertyName, value.toString());
			else if (type.equals(PrimitiveAccessor.TYPE_PASSWORD))
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
				Node curr = it.nextNode();

				// Manually skip aggregated status
				if (!SlcNames.SLC_AGGREGATED_STATUS.equals(curr.getName())) {
					Integer childStatus = aggregateTestStatus(curr);
					if (childStatus > status)
						status = childStatus;
				}
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
				// Manually skip aggregated status
				if (!SlcNames.SLC_AGGREGATED_STATUS.equals(child.getName())) {
					aggregateTestMessages(child, messages);
				}
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
}