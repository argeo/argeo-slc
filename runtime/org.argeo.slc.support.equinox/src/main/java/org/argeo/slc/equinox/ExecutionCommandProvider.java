package org.argeo.slc.equinox;

import java.util.List;

import org.argeo.slc.execution.ExecutionFlowDescriptor;
import org.argeo.slc.execution.ExecutionModule;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

public class ExecutionCommandProvider implements CommandProvider {
	private List<ExecutionModule> executionModules;

	public Object _slc_execute(CommandInterpreter ci) {
		String moduleName = ci.nextArgument();
		String executionName = ci.nextArgument();
		
		ExecutionModule module = null;
		for (ExecutionModule moduleT : executionModules) {
			if(moduleT.getName().equals(moduleName)){
				// TODO: check version
				module = moduleT;
				break;
			}
		}

		ExecutionFlowDescriptor descriptor = new ExecutionFlowDescriptor();
		descriptor.setName(executionName);
		if(module!=null)
			module.execute(descriptor);
		
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
