package org.argeo.slc.execution;

import java.util.List;

import org.argeo.slc.process.SlcExecution;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

public class ExecutionCommandProvider implements CommandProvider {
	private List<ExecutionModule> executionModules;

	public Object _slc_execute(CommandInterpreter ci) {
		String moduleName = ci.nextArgument();
		String executionName = ci.nextArgument();
		
		SlcExecution slcExecution = new SlcExecution();
		slcExecution.getAttributes().put("slc.flows", executionName);

		ExecutionModule module = null;
		for (ExecutionModule moduleT : executionModules) {
			if(moduleT.getName().equals(moduleName)){
				// TODO: check version
				module = moduleT;
				break;
			}
		}

		if(module!=null)
			module.execute(slcExecution);
		
		return null;
	}

	public String getHelp() {
		StringBuffer buf = new StringBuffer();
		buf.append("---SLC Execution Commands---\n");
		buf.append("\tslc_execute - Execute an execution flow\n");
		return buf.toString();

	}

	public void setExecutionModules(List<ExecutionModule> executionModules) {
		this.executionModules = executionModules;
	}

}
