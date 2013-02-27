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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.execution.ExecutionModuleDescriptor;
import org.argeo.slc.execution.ExecutionModulesManager;
import org.argeo.slc.execution.ExecutionProcess;
import org.argeo.slc.execution.SlcAgent;
import org.argeo.slc.execution.SlcAgentDescriptor;

/** Implements the base methods of an SLC agent. */
public class DefaultAgent implements SlcAgent {
	private final static Log log = LogFactory.getLog(DefaultAgent.class);

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
//		modulesManager.unregisterProcessNotifier(this,
//				new HashMap<String, String>());
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

	public void kill(ExecutionProcess process) {
		String processUuid = process.getUuid();
		if (runningProcesses.containsKey(processUuid)) {
			runningProcesses.get(processUuid).interrupt();
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
