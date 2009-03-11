package org.argeo.slc.core.execution;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.execution.ExecutionModule;
import org.argeo.slc.execution.ExecutionModuleDescriptor;
import org.argeo.slc.execution.ExecutionModulesManager;
import org.argeo.slc.process.SlcExecution;
import org.springframework.util.Assert;

public class DefaultModulesManager implements ExecutionModulesManager {
	private final static Log log = LogFactory
			.getLog(DefaultModulesManager.class);

	private List<ExecutionModule> executionModules = new ArrayList<ExecutionModule>();

	public ExecutionModuleDescriptor getExecutionModuleDescriptor(
			String moduleName, String version) {
		ExecutionModule module = null;
		for (ExecutionModule moduleT : executionModules) {
			if (moduleT.getName().equals(moduleName)) {
				// TODO: check version
				module = moduleT;
				break;
			}
		}

		Assert.notNull(module);

		return module.getDescriptor();
	}

	public List<ExecutionModule> listExecutionModules() {
		return executionModules;
	}

	public void setExecutionModules(List<ExecutionModule> executionModules) {
		this.executionModules = executionModules;
	}

	public void process(SlcExecution slcExecution) {
		log.info("SlcExecution " + slcExecution);

	}

}
