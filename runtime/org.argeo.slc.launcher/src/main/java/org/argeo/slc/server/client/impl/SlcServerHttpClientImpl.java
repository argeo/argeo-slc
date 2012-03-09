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
package org.argeo.slc.server.client.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.Condition;
import org.argeo.slc.SlcException;
import org.argeo.slc.execution.ExecutionFlowDescriptor;
import org.argeo.slc.execution.ExecutionModuleDescriptor;
import org.argeo.slc.msg.ExecutionAnswer;
import org.argeo.slc.msg.MsgConstants;
import org.argeo.slc.msg.ObjectList;
import org.argeo.slc.msg.event.SlcEvent;
import org.argeo.slc.process.RealizedFlow;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.runtime.SlcAgentDescriptor;
import org.argeo.slc.server.client.SlcServerHttpClient;

public class SlcServerHttpClientImpl extends AbstractHttpServicesClient
		implements SlcServerHttpClient {

	protected final static String PARAM_AGENT_ID = "agentId";

	private final static Log log = LogFactory
			.getLog(SlcServerHttpClientImpl.class);

	private Long serverReadyTimeout = 120 * 1000l;

	public void waitForSlcExecutionFinished(SlcExecution slcExecution,
			Long timeout) {
		if (slcExecution.getStatus().equals(SlcExecution.COMPLETED))
			return;

		long begin = System.currentTimeMillis();
		while (System.currentTimeMillis() - begin < timeout(timeout)) {
			SlcEvent event = pollEvent(timeout);
			String slcExecutionId = event.getHeaders().get(
					MsgConstants.PROPERTY_SLC_EXECUTION_ID);
			String status = event.getHeaders().get(
					MsgConstants.PROPERTY_SLC_EXECUTION_STATUS);
			if (slcExecutionId.equals(slcExecution.getUuid())
					&& status.equals(SlcExecution.COMPLETED)) {
				return;
			}
		}
		throw new SlcException("SLC Execution not completed after timeout "
				+ timeout(timeout) + " elapsed.");
	}

	public SlcEvent pollEvent(Long timeout) {
		long begin = System.currentTimeMillis();
		while (System.currentTimeMillis() - begin < timeout(timeout)) {
			Object obj = callService(POLL_EVENT, null);
			if (obj instanceof ExecutionAnswer) {
				ExecutionAnswer answer = (ExecutionAnswer) obj;
				if (answer.isError())
					throw new SlcException(
							"Unexpected exception when polling event: "
									+ answer.getMessage());
			} else {
				return (SlcEvent) obj;
			}
		}
		throw new SlcException("No event received after timeout "
				+ timeout(timeout) + " elapsed.");
	}

	public ExecutionAnswer addEventListener(String eventType, String eventFilter) {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(SlcEvent.EVENT_TYPE, eventType);
		parameters.put(SlcEvent.EVENT_FILTER, eventFilter);
		return callService(ADD_EVENT_LISTENER, parameters);
	}

	public ExecutionAnswer removeEventListener(String eventType,
			String eventFilter) {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(SlcEvent.EVENT_TYPE, eventType);
		parameters.put(SlcEvent.EVENT_FILTER, eventFilter);
		return callService(REMOVE_EVENT_LISTENER, parameters);
	}

	public SlcExecution startFlow(String agentId, RealizedFlow realizedFlow) {
		SlcExecution slcExecution = new SlcExecution();
		slcExecution.setUuid(UUID.randomUUID().toString());

		slcExecution.getRealizedFlows().add(realizedFlow);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(MsgConstants.PROPERTY_SLC_AGENT_ID, agentId);
		ExecutionAnswer answer = callService(NEW_SLC_EXECUTION, parameters,
				slcExecution);
		if (!answer.isOk())
			throw new SlcException("Could not start flow on agent " + agentId
					+ ": " + answer.getMessage());
		return slcExecution;
	}

	public SlcExecution startFlowDefault(String moduleName, String flowName,
			Map<String, Object> args) {
		SlcAgentDescriptor agentDescriptor = waitForOneAgent();
		List<ExecutionModuleDescriptor> lst = listModuleDescriptors(agentDescriptor
				.getUuid());
		ExecutionModuleDescriptor moduleDescMinimal = findModule(lst,
				moduleName);
		if (moduleDescMinimal == null)
			throw new SlcException("Cannot find module " + moduleName);
		String moduleVersion = moduleDescMinimal.getVersion();

		ExecutionModuleDescriptor moduleDesc = getModuleDescriptor(
				agentDescriptor.getUuid(), moduleName, moduleVersion);

		RealizedFlow realizedFlow = new RealizedFlow();
		realizedFlow.setModuleName(moduleName);
		realizedFlow.setModuleVersion(moduleDesc.getVersion());

		ExecutionFlowDescriptor flowDescriptor = findFlow(moduleDesc, flowName);
		if (args != null) {
			for (String key : args.keySet()) {
				if (flowDescriptor.getValues().containsKey(key)) {
					flowDescriptor.getValues().put(key, args.get(key));
				}
			}
		}
		realizedFlow.setFlowDescriptor(flowDescriptor);

		return startFlow(agentDescriptor.getUuid(), realizedFlow);

		// FIXME: polling not working when called from test: no unique
		// session is created on server side
		// SlcExecution slcExecutionFinished = null;
		// try {
		// addEventListener(
		// EventPublisherAspect.EVT_UPDATE_SLC_EXECUTION_STATUS, null);
		// SlcExecution slcExecution = startFlow(agentDescriptor.getUuid(),
		// realizedFlow);
		//
		// waitForSlcExecutionFinished(slcExecution, null);
		//
		// ObjectList ol = callService(LIST_SLC_EXECUTIONS, null);
		// for (Serializable sr : ol.getObjects()) {
		// SlcExecution se = (SlcExecution) sr;
		// if (se.getUuid().equals(slcExecution.getUuid())) {
		// slcExecutionFinished = se;
		// break;
		// }
		// }
		//
		// } finally {
		// removeEventListener(
		// EventPublisherAspect.EVT_UPDATE_SLC_EXECUTION_STATUS, null);
		// }
		//
		// if (slcExecutionFinished == null)
		// throw new SlcException("No finished SLC Execution.");
		// return slcExecutionFinished;
	}

	public static ExecutionModuleDescriptor findModule(
			List<ExecutionModuleDescriptor> lst, String moduleName) {
		ExecutionModuleDescriptor moduleDesc = null;
		for (ExecutionModuleDescriptor desc : lst) {
			if (desc.getName().equals(moduleName)) {
				if (moduleDesc != null)
					throw new SlcException(
							"There is more than one module named " + moduleName
									+ " (versions: " + moduleDesc + " and "
									+ desc.getVersion() + ")");
				moduleDesc = desc;
			}
		}
		return moduleDesc;
	}

	public static ExecutionFlowDescriptor findFlow(
			ExecutionModuleDescriptor moduleDesc, String flowName) {
		ExecutionFlowDescriptor flowDesc = null;
		for (ExecutionFlowDescriptor desc : moduleDesc.getExecutionFlows()) {
			if (desc.getName().equals(flowName)) {
				flowDesc = desc;
			}
		}
		return flowDesc;
	}

	public List<ExecutionModuleDescriptor> listModuleDescriptors(String agentId) {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(PARAM_AGENT_ID, agentId);

		List<ExecutionModuleDescriptor> moduleDescriptors = new ArrayList<ExecutionModuleDescriptor>();
		ObjectList ol = callService(LIST_MODULE_DESCRIPTORS, parameters);
		ol.fill(moduleDescriptors);
		return moduleDescriptors;
	}

	public ExecutionModuleDescriptor getModuleDescriptor(String agentId,
			String moduleName, String version) {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(PARAM_AGENT_ID, agentId);
		parameters.put("moduleName", moduleName);
		parameters.put("version", version);
		ExecutionModuleDescriptor moduleDescriptor = callService(
				GET_MODULE_DESCRIPTOR, parameters);
		return moduleDescriptor;
	}

	public SlcAgentDescriptor waitForOneAgent() {
		ObjectList objectList = callServiceSafe(LIST_AGENTS, null,
				new Condition<ObjectList>() {
					public Boolean check(ObjectList obj) {
						int size = obj.getObjects().size();
						if (log.isTraceEnabled())
							log.trace("Object list size: " + size);
						return size == 1;
					}
				}, null);
		return (SlcAgentDescriptor) objectList.getObjects().get(0);
	}

	public void waitForServerToBeReady() {
		ExecutionAnswer answer = callServiceSafe(IS_SERVER_READY, null, null,
				serverReadyTimeout);
		if (!answer.isOk())
			throw new SlcException("Server is not ready: " + answer);
	}

	/**
	 * Timeout in ms after which the client will stop waiting for the server to
	 * be ready and throw an exception. Default is 120s.
	 */
	public void setServerReadyTimeout(Long serverReadyTimeout) {
		this.serverReadyTimeout = serverReadyTimeout;
	}

}
