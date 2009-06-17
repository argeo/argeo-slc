package org.argeo.slc.server;

public interface HttpServices {
	public final static String LIST_AGENTS = "listAgents.service";
	public final static String IS_SERVER_READY = "isServerReady.service";
	public final static String NEW_SLC_EXECUTION = "newSlcExecution.service";
	public final static String LIST_SLC_EXECUTIONS = "listSlcExecutions.service";
	public final static String GET_MODULE_DESCRIPTOR = "getExecutionDescriptor.service";
	public final static String LIST_MODULE_DESCRIPTORS = "listModulesDescriptors.service";
	public final static String LIST_RESULTS = "listResults.service";
	public final static String ADD_EVENT_LISTENER = "addEventListener.service";
	public final static String REMOVE_EVENT_LISTENER = "removeEventListener.service";
	public final static String POLL_EVENT = "pollEvent.service";

}
