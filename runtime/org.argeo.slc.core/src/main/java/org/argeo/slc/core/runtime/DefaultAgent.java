package org.argeo.slc.core.runtime;

import java.util.List;

import org.argeo.slc.execution.ExecutionModuleDescriptor;
import org.argeo.slc.execution.ExecutionModulesManager;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.runtime.SlcAgent;

public class DefaultAgent implements SlcAgent {
	// private final static Log log = LogFactory.getLog(AbstractAgent.class);

	private ExecutionModulesManager modulesManager;

	public void runSlcExecution(final SlcExecution slcExecution) {
		modulesManager.process(slcExecution);
	}

	public ExecutionModuleDescriptor getExecutionModuleDescriptor(
			String moduleName, String version) {
		return modulesManager.getExecutionModuleDescriptor(moduleName,
				version);
	}

	public List<ExecutionModuleDescriptor> listExecutionModuleDescriptors() {
		return modulesManager.listExecutionModules();
	}

	public boolean ping() {
		return true;
	}

	public void setModulesManager(ExecutionModulesManager modulesManager) {
		this.modulesManager = modulesManager;
	}

	public ExecutionModulesManager getModulesManager() {
		return modulesManager;
	}

}
