package org.argeo.slc.server.client.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.Condition;
import org.argeo.slc.SlcException;
import org.argeo.slc.execution.ExecutionFlowDescriptor;
import org.argeo.slc.msg.ExecutionAnswer;
import org.argeo.slc.msg.MsgConstants;
import org.argeo.slc.msg.ObjectList;
import org.argeo.slc.process.RealizedFlow;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.runtime.SlcAgentDescriptor;
import org.argeo.slc.server.client.SlcServerHttpClient;

public class SlcServerHttpClientImpl extends AbstractHttpServicesClient
		implements SlcServerHttpClient {
	public final static String LIST_AGENTS = "listAgents.service";
	public final static String IS_SERVER_READY = "isServerReady.service";
	public final static String NEW_SLC_EXECUTION = "newSlcExecution.service";

	private final static Log log = LogFactory
			.getLog(SlcServerHttpClientImpl.class);

	private Long retryTimeout = 60 * 1000l;
	private Long serverReadyTimeout = 120 * 1000l;

	public ExecutionAnswer startFlow(String agentId, String moduleName,
			String version, String flowName) {
		SlcExecution slcExecution = new SlcExecution();
		slcExecution.setUuid(UUID.randomUUID().toString());

		RealizedFlow realizedFlow = new RealizedFlow();
		realizedFlow.setModuleName(moduleName);
		realizedFlow.setModuleVersion(version);

		ExecutionFlowDescriptor flowDescriptor = new ExecutionFlowDescriptor();
		flowDescriptor.setName(flowName);

		realizedFlow.setFlowDescriptor(flowDescriptor);
		slcExecution.getRealizedFlows().add(realizedFlow);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(MsgConstants.PROPERTY_SLC_AGENT_ID, agentId);
		ExecutionAnswer answer = callService(NEW_SLC_EXECUTION, parameters,
				slcExecution);
		return answer;
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
				}, retryTimeout);
		return (SlcAgentDescriptor) objectList.getObjects().get(0);
	}

	public void waitForServerToBeReady() {
		ExecutionAnswer answer = callServiceSafe(IS_SERVER_READY, null, null,
				serverReadyTimeout);
		if (!answer.isOk())
			throw new SlcException("Server is not ready: " + answer);
	}

	/**
	 * Timeout in ms after which a safe call will throw an exception. Default is
	 * 60s.
	 */
	public void setRetryTimeout(Long retryTimeout) {
		this.retryTimeout = retryTimeout;
	}

	/**
	 * Timeout in ms after which the client will stop waiting for the server to
	 * be ready and throw an exception. Default is 120s.
	 */
	public void setServerReadyTimeout(Long serverReadyTimeout) {
		this.serverReadyTimeout = serverReadyTimeout;
	}

}
