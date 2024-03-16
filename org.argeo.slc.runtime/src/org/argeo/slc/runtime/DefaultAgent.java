package org.argeo.slc.runtime;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.argeo.api.slc.DefaultNameVersion;
import org.argeo.api.slc.NameVersion;
import org.argeo.api.slc.SlcException;
import org.argeo.api.slc.execution.ExecutionModuleDescriptor;
import org.argeo.api.slc.execution.ExecutionModulesManager;
import org.argeo.api.slc.execution.ExecutionProcess;
import org.argeo.api.slc.execution.SlcAgent;

/** Implements the base methods of an SLC agent. */
public class DefaultAgent implements SlcAgent {
	// private final static CmsLog log = CmsLog.getLog(DefaultAgent.class);
	/** UTF-8 charset for encoding. */
	private final static String UTF8 = "UTF-8";

	private String agentUuid = null;
	private ExecutionModulesManager modulesManager;

	private ThreadGroup processesThreadGroup;
	private Map<String, ProcessThread> runningProcesses = Collections
			.synchronizedMap(new HashMap<String, ProcessThread>());

	private String defaultModulePrefix = null;

	/*
	 * LIFECYCLE
	 */
	/** Initialization */
	public void init() {
		agentUuid = initAgentUuid();
		processesThreadGroup = new ThreadGroup("SLC Processes of Agent #"
				+ agentUuid);
	}

	/** Clean up (needs to be called by overriding method) */
	public void destroy() {
	}

	/**
	 * Called during initialization in order to determines the agent UUID. To be
	 * overridden. By default creates a new one per instance.
	 */
	protected String initAgentUuid() {
		return UUID.randomUUID().toString();
	}

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
			NameVersion nameVersion = new DefaultNameVersion(path[1]);
			StringBuilder flow = new StringBuilder();
			for (int i = 2; i < path.length; i++)
				flow.append('/').append(path[i]);

			Map<String, Object> values = getQueryMap(uri.getQuery());
			// Get execution module descriptor
			ExecutionModuleDescriptor emd = getExecutionModuleDescriptor(
					nameVersion.getName(), nameVersion.getVersion());
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
			String moduleName, String moduleVersion) {
		// Get execution module descriptor
		ExecutionModuleDescriptor emd;
		try {
			modulesManager
					.start(new DefaultNameVersion(moduleName, moduleVersion));
			emd = modulesManager.getExecutionModuleDescriptor(moduleName,
					moduleVersion);
		} catch (SlcException e) {
			if (defaultModulePrefix != null) {
				moduleName = defaultModulePrefix + "." + moduleName;
				modulesManager.start(new DefaultNameVersion(moduleName,
						moduleVersion));
				emd = modulesManager.getExecutionModuleDescriptor(moduleName,
						moduleVersion);
			} else
				throw e;
		}
		return emd;
	}

	public List<ExecutionModuleDescriptor> listExecutionModuleDescriptors() {
		return modulesManager.listExecutionModules();
	}

	public boolean ping() {
		return true;
	}

	/*
	 * UTILITIES
	 */
	/**
	 * @param query
	 *            can be null
	 */
	static Map<String, Object> getQueryMap(String query) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		if (query == null)
			return map;
		String[] params = query.split("&");
		for (String param : params) {
			String[] arr = param.split("=");
			String name = arr[0];
			Object value = arr.length > 1 ? param.split("=")[1] : Boolean.TRUE;
			try {
				map.put(URLDecoder.decode(name, UTF8),
						URLDecoder.decode(value.toString(), UTF8));
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

	public void setDefaultModulePrefix(String defaultModulePrefix) {
		this.defaultModulePrefix = defaultModulePrefix;
	}

	public String getAgentUuid() {
		return agentUuid;
	}

	@Override
	public String toString() {
		return "Agent #" + getAgentUuid();
	}
}
