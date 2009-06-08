package org.argeo.slc.server.client;

import java.util.List;
import java.util.Map;

import org.argeo.slc.execution.ExecutionModuleDescriptor;
import org.argeo.slc.msg.ExecutionAnswer;
import org.argeo.slc.process.RealizedFlow;
import org.argeo.slc.runtime.SlcAgentDescriptor;

/** Abstraction of the access to HTTP services of an SLC Server. */
public interface SlcServerHttpClient extends HttpServicesClient {
	public final static String LIST_AGENTS = "listAgents.service";
	public final static String IS_SERVER_READY = "isServerReady.service";
	public final static String NEW_SLC_EXECUTION = "newSlcExecution.service";
	public final static String GET_MODULE_DESCRIPTOR = "getExecutionDescriptor.service";
	public final static String LIST_MODULE_DESCRIPTORS = "listModulesDescriptors.service";
	public final static String LIST_RESULTS = "listResults.service";

	/** Wait for one agent to be available. */
	public SlcAgentDescriptor waitForOneAgent();

	/** Wait for the http server to be ready. */
	public void waitForServerToBeReady();

	/** Start an execution flow on the given agent. */
	public ExecutionAnswer startFlow(String agentId, RealizedFlow realizedFlow);

	/** Assume one agent and one version per module. */
	public ExecutionAnswer startFlowDefault(String moduleName, String flowName,
			Map<String, Object> args);

	/** List execution modules descriptors. */
	public List<ExecutionModuleDescriptor> listModuleDescriptors(String agentId);

	/** Retrieve a single execution module descriptot. */
	public ExecutionModuleDescriptor getModuleDescriptor(String agentId,
			String moduleName, String version);

}
