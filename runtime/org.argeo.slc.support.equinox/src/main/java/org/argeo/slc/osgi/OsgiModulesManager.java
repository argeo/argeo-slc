package org.argeo.slc.osgi;

import java.util.List;

import org.argeo.slc.core.execution.DefaultModulesManager;
import org.argeo.slc.execution.ExecutionModule;

public class OsgiModulesManager extends DefaultModulesManager {


	@Override
	protected ExecutionModule getExecutionModule(String moduleName,
			String version) {
		// TODO Auto-generated method stub
		return super.getExecutionModule(moduleName, version);
	}

	@Override
	public List<ExecutionModule> listExecutionModules() {
		// TODO Auto-generated method stub
		return super.listExecutionModules();
	}

}
