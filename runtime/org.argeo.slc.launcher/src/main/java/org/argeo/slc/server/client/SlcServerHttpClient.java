/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
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
