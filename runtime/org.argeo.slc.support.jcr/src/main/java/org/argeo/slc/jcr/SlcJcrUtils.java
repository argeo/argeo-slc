package org.argeo.slc.jcr;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.core.execution.PrimitiveAccessor;
import org.argeo.slc.core.execution.PrimitiveUtils;
import org.argeo.slc.deploy.ModuleDescriptor;

/**
 * Utilities around the SLC JCR model. Note that it relies on fixed base paths
 * (convention over configuration) for optimization purposes.
 */
public class SlcJcrUtils {
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

	/** Map a primitive value to JCR ptoperty value. */
	public static void setPrimitiveAsProperty(Node node, String propertyName,
			String type, Object value) {
		if (value == null)
			return;
		if (value instanceof CharSequence)
			value = PrimitiveUtils.convert(type,
					((CharSequence) value).toString());

		try {
			if (type.equals(PrimitiveUtils.TYPE_STRING))
				node.setProperty(propertyName, value.toString());
			else if (type.equals(PrimitiveUtils.TYPE_INTEGER))
				node.setProperty(propertyName, (long) ((Integer) value));
			else if (type.equals(PrimitiveUtils.TYPE_LONG))
				node.setProperty(propertyName, ((Long) value));
			else if (type.equals(PrimitiveUtils.TYPE_FLOAT))
				node.setProperty(propertyName, (double) ((Float) value));
			else if (type.equals(PrimitiveUtils.TYPE_DOUBLE))
				node.setProperty(propertyName, ((Double) value));
			else if (type.equals(PrimitiveUtils.TYPE_BOOLEAN))
				node.setProperty(propertyName, ((Boolean) value));
			else
				throw new SlcException("Unsupported type " + type);
		} catch (RepositoryException e) {
			throw new SlcException("Cannot set primitive of " + type
					+ " as property " + propertyName + " on " + node, e);
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
