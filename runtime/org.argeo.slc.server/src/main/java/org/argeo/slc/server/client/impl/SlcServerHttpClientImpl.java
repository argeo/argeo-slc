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
import org.argeo.slc.process.RealizedFlow;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.runtime.SlcAgentDescriptor;
import org.argeo.slc.server.client.SlcServerHttpClient;

public class SlcServerHttpClientImpl extends AbstractHttpServicesClient
		implements SlcServerHttpClient {

	protected final static String PARAM_AGENT_ID = "agentId";

	private final static Log log = LogFactory
			.getLog(SlcServerHttpClientImpl.class);

	private Long retryTimeout = 60 * 1000l;
	private Long serverReadyTimeout = 120 * 1000l;

	public ExecutionAnswer startFlow(String agentId, RealizedFlow realizedFlow) {
		SlcExecution slcExecution = new SlcExecution();
		slcExecution.setUuid(UUID.randomUUID().toString());

		slcExecution.getRealizedFlows().add(realizedFlow);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(MsgConstants.PROPERTY_SLC_AGENT_ID, agentId);
		ExecutionAnswer answer = callService(NEW_SLC_EXECUTION, parameters,
				slcExecution);
		return answer;
	}

	public ExecutionAnswer startFlowDefault(String moduleName, String flowName,
			Map<String, Object> args) {
		SlcAgentDescriptor agentDescriptor = waitForOneAgent();
		List<ExecutionModuleDescriptor> lst = listModuleDescriptors(agentDescriptor
				.getUuid());
		ExecutionModuleDescriptor moduleDescMinimal = findModule(lst,
				moduleName);
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
