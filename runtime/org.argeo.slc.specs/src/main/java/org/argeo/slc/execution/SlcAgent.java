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
package org.argeo.slc.execution;

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
