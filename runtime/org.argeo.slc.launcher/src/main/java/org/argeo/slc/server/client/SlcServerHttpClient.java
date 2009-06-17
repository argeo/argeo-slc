package org.argeo.slc.server.client;

import java.util.List;
import java.util.Map;

import org.argeo.slc.execution.ExecutionModuleDescriptor;
import org.argeo.slc.msg.ExecutionAnswer;
import org.argeo.slc.msg.event.SlcEvent;
import org.argeo.slc.process.RealizedFlow;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.runtime.SlcAgentDescriptor;
import org.argeo.slc.server.HttpServices;

/** Abstraction of the access to HTTP services of an SLC Server. */
public interface SlcServerHttpClient extends HttpServicesClient,HttpServices {
	/** Wait for the provided SlcExecution to be finished. */
	public void waitForSlcExecutionFinished(SlcExecution slcExecution,
			Long timeout);

	/** Block until one of the registered event is finished. */
	public SlcEvent pollEvent(Long timeout);

	/** Register an event type. */
	public ExecutionAnswer addEventListener(String eventType, String eventFilter);

	/** Unregister an event type. */
	public ExecutionAnswer removeEventListener(String eventType,
			String eventFilter);

	/** Wait for one agent to be available. */
	public SlcAgentDescriptor waitForOneAgent();

	/** Wait for the http server to be ready. */
	public void waitForServerToBeReady();

	/** Start an execution flow on the given agent. */
	public SlcExecution startFlow(String agentId, RealizedFlow realizedFlow);

	/** Assume one agent and one version per module. */
	public SlcExecution startFlowDefault(String moduleName, String flowName,
			Map<String, Object> args);

	/** List execution modules descriptors. */
	public List<ExecutionModuleDescriptor> listModuleDescriptors(String agentId);

	/** Retrieve a single execution module descriptor. */
	public ExecutionModuleDescriptor getModuleDescriptor(String agentId,
			String moduleName, String version);

}
