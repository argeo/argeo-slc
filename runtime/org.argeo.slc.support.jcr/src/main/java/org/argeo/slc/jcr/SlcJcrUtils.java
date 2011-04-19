package org.argeo.slc.jcr;

/** Utilities around the SLC JCR model. Note that it relies on fixed base paths. */
public class SlcJcrUtils {
	public final static Integer AGENT_FACTORY_DEPTH = 3;
	public final static Integer EXECUTION_MODULES_DEPTH = AGENT_FACTORY_DEPTH + 2;
	public final static Integer EXECUTION_FLOWS_DEPTH = EXECUTION_MODULES_DEPTH + 3;

	/** Extracts the path of a flow relative to its execution module */
	public static String flowRelativePath(String fullFlowPath) {
		String[] tokens = fullFlowPath.split("/");
		StringBuffer buf = new StringBuffer(fullFlowPath.length());
		for (int i = EXECUTION_FLOWS_DEPTH; i < tokens.length; i++) {
			buf.append('/').append(tokens[i]);
		}
		return buf.toString();
	}

	/** Extracts the execution module name of a flow */
	public static String flowExecutionModuleName(String fullFlowPath) {
		String[] tokens = fullFlowPath.split("/");
		return tokens[EXECUTION_MODULES_DEPTH + 1];
	}

	/** Extracts the execution module version of a flow */
	public static String flowExecutionModuleVersion(String fullFlowPath) {
		String[] tokens = fullFlowPath.split("/");
		return tokens[EXECUTION_MODULES_DEPTH + 2];
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

	/** Prevents instantiation */
	private SlcJcrUtils() {

	}

	// public static void main(String[] args) {
	// String path =
	// "/slc/agents/vm/54654654654/executionModules/org.argeo/1.2.3/myFlow";
	// System.out.println(flowRelativePath(path));
	// System.out.println(flowExecutionModuleName(path));
	// System.out.println(flowAgentFactoryPath(path));
	// }
}
