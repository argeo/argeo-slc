package org.argeo.slc.core.execution;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.execution.ExecutionFlow;
import org.argeo.slc.execution.ExecutionFlowDescriptor;
import org.argeo.slc.execution.ExecutionModule;
import org.argeo.slc.execution.ExecutionModuleDescriptor;
import org.argeo.slc.execution.ExecutionModulesManager;
import org.argeo.slc.process.RealizedFlow;
import org.argeo.slc.process.SlcExecution;
import org.springframework.util.Assert;

public class DefaultModulesManager implements ExecutionModulesManager {
	private final static Log log = LogFactory
			.getLog(DefaultModulesManager.class);

	private List<ExecutionModule> executionModules = new ArrayList<ExecutionModule>();
	
	protected ExecutionModule getExecutionModule(String moduleName, String version) {
		for (ExecutionModule moduleT : executionModules) {
			if (moduleT.getName().equals(moduleName)) {
				// TODO: check version
				return moduleT;
			}
		}
		return null;
	}
	
	public ExecutionModuleDescriptor getExecutionModuleDescriptor(
			String moduleName, String version) {
		ExecutionModule module = getExecutionModule(moduleName, version);
		
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
		log.info("##\n## Process SLC Execution " + slcExecution+"\n##");

		for(RealizedFlow flow : slcExecution.getRealizedFlows()) {
			ExecutionModule module = getExecutionModule(flow.getModuleName(),
					flow.getModuleVersion());
			if(module != null) {
				ExecutionContext executionContext = new ExecutionContext();
				executionContext.addVariables(slcExecution.getAttributes());
				ExecutionThread thread = new ExecutionThread(executionContext, flow.getFlowDescriptor(),
						module);
				thread.start();
			}
			else {
				// throw exception ?
			}
		}
	}

	private class ExecutionThread extends Thread {
		private final ExecutionFlowDescriptor executionFlowDescriptor;
		private final ExecutionContext executionContext;
		private final ExecutionModule executionModule;

		public ExecutionThread(ExecutionContext executionContext,
				ExecutionFlowDescriptor executionFlowDescriptor,
				ExecutionModule executionModule) {
			super("SLC Execution #" + executionContext.getUuid());
			this.executionFlowDescriptor = executionFlowDescriptor;
			this.executionContext = executionContext;
			this.executionModule = executionModule;
		}

		public void run() {
			ExecutionContext.registerExecutionContext(executionContext);				
			try {
				executionModule.execute(executionFlowDescriptor);
			} catch (Exception e) {
				log.error("Execution " + executionContext.getUuid()
						+ " failed.", e);
			}
		}
	}	
	
}
