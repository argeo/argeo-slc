package org.argeo.slc.core.runtime;

import org.argeo.slc.execution.ExecutionModulesManager;
import org.argeo.slc.process.SlcExecution;

public abstract class AbstractAgent {
//	private final static Log log = LogFactory.getLog(AbstractAgent.class);

	private ExecutionModulesManager modulesManager;

	public void runSlcExecution(final SlcExecution slcExecution) {
		modulesManager.process(slcExecution);
	}

	public void setModulesManager(ExecutionModulesManager modulesManager) {
		this.modulesManager = modulesManager;
	}

	public ExecutionModulesManager getModulesManager() {
		return modulesManager;
	}

	
}
