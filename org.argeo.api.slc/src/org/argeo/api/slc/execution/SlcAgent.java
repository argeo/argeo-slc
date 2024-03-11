package org.argeo.api.slc.execution;

import java.net.URI;
import java.util.List;

/**
 * A local agent can run SLC processes. It is responsible for creating their
 * threads and integrating them with various UIs. It typically wraps
 * {@link ExecutionModulesManager} which is used to run flows synchronously at a
 * lower level.
 */
public interface SlcAgent {
	/** Agent unique identifier */
	public String getAgentUuid();

	/** Execute / take part to this process */
	public void process(ExecutionProcess process);

	/**
	 * Asynchronously processes the flows defined as URIs, or interpret a single
	 * UUID URN as a scheduled or template process.
	 * 
	 * @return the UUID of the process launched.
	 */
	public String process(List<URI> uris);

	/** Kills this process */
	public void kill(String processUuid);

	/**
	 * Wait for this process to finish. returns immediately if it does not
	 * exist.
	 * 
	 * @param millis
	 *            can be null
	 */
	public void waitFor(String processUuid, Long millis);

	/**
	 * Describe all the flows provided by this execution module. Typically
	 * called in order to build a realized flow.
	 */
	public ExecutionModuleDescriptor getExecutionModuleDescriptor(
			String moduleName, String version);

	/** List all execution modules which can be processed by this agent. */
	public List<ExecutionModuleDescriptor> listExecutionModuleDescriptors();

	/** @return true if still alive. */
	public boolean ping();
}
