package org.argeo.slc.server.client;

import org.argeo.slc.msg.ExecutionAnswer;
import org.argeo.slc.runtime.SlcAgentDescriptor;

/** Abstraction of the access to HTTP services of an SLC Server. */
public interface SlcServerHttpClient extends HttpServicesClient {
	/** Wait for one agent to be available. */
	public SlcAgentDescriptor waitForOneAgent();

	/** Wait for the http server to be ready. */
	public void waitForServerToBeReady();

	/** Start an execution flow on the given agent. */
	public ExecutionAnswer startFlow(String agentId, String moduleName,
			String version, String flowName);
}
