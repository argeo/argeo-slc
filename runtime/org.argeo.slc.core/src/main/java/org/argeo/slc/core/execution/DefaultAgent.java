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
package org.argeo.slc.core.execution;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.BasicNameVersion;
import org.argeo.slc.SlcException;
import org.argeo.slc.execution.ExecutionModuleDescriptor;
import org.argeo.slc.execution.ExecutionModulesManager;
import org.argeo.slc.execution.ExecutionProcess;
import org.argeo.slc.execution.SlcAgent;
import org.argeo.slc.execution.SlcAgentDescriptor;

/** Implements the base methods of an SLC agent. */
public class DefaultAgent implements SlcAgent {
	private final static Log log = LogFactory.getLog(DefaultAgent.class);
	/** UTF-8 charset for encoding. */
	private final static String UTF8 = "UTF-8";

	private SlcAgentDescriptor agentDescriptor;
	private ExecutionModulesManager modulesManager;

	private ThreadGroup processesThreadGroup;
	private Map<String, ProcessThread> runningProcesses = Collections
			.synchronizedMap(new HashMap<String, ProcessThread>());

	/*
	 * LIFECYCLE
	 */
	/** Initialization */
	public void init() {
		agentDescriptor = new SlcAgentDescriptor();
		agentDescriptor.setUuid(initAgentUuid());
		try {
			agentDescriptor.setHost(InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			log.error("Cannot resolve localhost host name: " + e.getMessage());
			agentDescriptor.setHost("localhost");
		}
		processesThreadGroup = new ThreadGroup("SLC Processes of Agent #"
				+ agentDescriptor.getUuid());
		// modulesManager.registerProcessNotifier(this,
		// new HashMap<String, String>());

		// final String module = System
		// .getProperty(ExecutionModulesManager.UNIQUE_LAUNCH_MODULE_PROPERTY);
		// final String flow = System
		// .getProperty(ExecutionModulesManager.UNIQUE_LAUNCH_FLOW_PROPERTY);
		// if (module != null) {
		// // launch a flow and stops
		// new Thread("Unique Flow") {
		// @Override
		// public void run() {
		// executeFlowAndExit(module, null, flow);
		// }
		// }.start();
		// }
	}

	/** Clean up (needs to be called by overriding method) */
	public void destroy() {
		// modulesManager.unregisterProcessNotifier(this,
		// new HashMap<String, String>());
	}

	/**
	 * Called during initialization in order to determines the agent UUID. To be
	 * overridden. By default creates a new one per instance.
	 */
	protected String initAgentUuid() {
		return UUID.randomUUID().toString();
	}

	/*
	 * UNIQUE FLOW
	 */
	// protected void executeFlowAndExit(final String module,
	// final String version, final String flow) {
	// }

	/*
	 * SLC AGENT
	 */
	public void process(ExecutionProcess process) {
		ProcessThread processThread = createProcessThread(processesThreadGroup,
				modulesManager, process);
		processThread.start();
		runningProcesses.put(process.getUuid(), processThread);

		// clean up old processes
		Iterator<ProcessThread> it = runningProcesses.values().iterator();
		while (it.hasNext()) {
			ProcessThread pThread = it.next();
			if (!pThread.isAlive())
				it.remove();
		}
	}

	public String process(List<URI> uris) {
		DefaultProcess process = new DefaultProcess();
		for (URI uri : uris) {
			String[] path = uri.getPath().split("/");
			if (path.length < 3)
				throw new SlcException("Badly formatted URI: " + uri);
			String module = path[1];
			StringBuilder flow = new StringBuilder();
			for (int i = 2; i < path.length; i++)
				flow.append('/').append(path[i]);

			Map<String, Object> values = new HashMap<String, Object>();
			if (uri.getQuery() != null)
				values = getQueryMap(uri.getQuery());

			modulesManager.start(new BasicNameVersion(module, null));
			ExecutionModuleDescriptor emd = getExecutionModuleDescriptor(
					module, null);
			process.getRealizedFlows().add(
					emd.asRealizedFlow(flow.toString(), values));
		}
		process(process);
		return process.getUuid();
	}

	public void kill(String processUuid) {
		if (runningProcesses.containsKey(processUuid)) {
			runningProcesses.get(processUuid).interrupt();
		} else {
			// assume is finished
		}
	}

	public void waitFor(String processUuid, Long millis) {
		if (runningProcesses.containsKey(processUuid)) {
			try {
				if (millis != null)
					runningProcesses.get(processUuid).join(millis);
				else
					runningProcesses.get(processUuid).join();
			} catch (InterruptedException e) {
				// silent
			}
		} else {
			// assume is finished
		}
	}

	/** Creates the thread which will coordinate the execution for this agent. */
	protected ProcessThread createProcessThread(
			ThreadGroup processesThreadGroup,
			ExecutionModulesManager modulesManager, ExecutionProcess process) {
		ProcessThread processThread = new ProcessThread(processesThreadGroup,
				modulesManager, process);
		return processThread;
	}

	public ExecutionModuleDescriptor getExecutionModuleDescriptor(
			String moduleName, String version) {
		return modulesManager.getExecutionModuleDescriptor(moduleName, version);
	}

	public List<ExecutionModuleDescriptor> listExecutionModuleDescriptors() {
		return modulesManager.listExecutionModules();
	}

	public boolean ping() {
		return true;
	}

	/*
	 * PROCESS NOTIFIER
	 */
	// public void updateStatus(ExecutionProcess process, String oldStatus,
	// String newStatus) {
	// if (newStatus.equals(ExecutionProcess.COMPLETED)
	// || newStatus.equals(ExecutionProcess.ERROR)
	// || newStatus.equals(ExecutionProcess.KILLED)) {
	// runningProcesses.remove(process.getUuid());
	// }
	// }
	//
	// public void addSteps(ExecutionProcess process, List<ExecutionStep> steps)
	// {
	// }

	/*
	 * UTILITIES
	 */
	private static Map<String, Object> getQueryMap(String query) {
		String[] params = query.split("&");
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		for (String param : params) {
			String name = param.split("=")[0];
			String value = param.split("=")[1];
			try {
				map.put(URLDecoder.decode(name, UTF8),
						URLDecoder.decode(value, UTF8));
			} catch (UnsupportedEncodingException e) {
				throw new SlcException("Cannot decode '" + param + "'", e);
			}
		}
		return map;
	}

	/*
	 * BEAN
	 */
	public void setModulesManager(ExecutionModulesManager modulesManager) {
		this.modulesManager = modulesManager;
	}

	protected SlcAgentDescriptor getAgentDescriptor() {
		return agentDescriptor;
	}

	public String getAgentUuid() {
		return agentDescriptor.getUuid();
	}

	@Override
	public String toString() {
		return agentDescriptor.toString();
	}
}
