package org.argeo.slc.client.ui.commands;

import java.util.HashMap;
import java.util.Map;

import org.argeo.slc.SlcException;
import org.argeo.slc.execution.ExecutionFlowDescriptor;
import org.argeo.slc.execution.ExecutionModulesManager;
import org.argeo.slc.execution.RealizedFlow;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IParameter;

@Deprecated
public class RunSlcFlow extends AbstractHandler {
	private ExecutionModulesManager modulesManager;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			Command command = event.getCommand();
			String name = command.getName();
			String module = name.substring(0, name.indexOf(':'));
			String flowName = name.substring(name.indexOf(':') + 1);

			final RealizedFlow realizedFlow = new RealizedFlow();
			realizedFlow.setModuleName(module);
			// FIXME deal with version
			String version = "0.0.0";
			realizedFlow.setModuleVersion(version);
			ExecutionFlowDescriptor efd = new ExecutionFlowDescriptor();
			efd.setName(flowName);

			Map<String, Object> values = new HashMap<String, Object>();
			if (command.getParameters() != null) {
				for (IParameter param : command.getParameters()) {
					String argName = param.getId();
					// FIXME make it safer
					Object value = param.getValues().getParameterValues()
							.values().iterator().next();
					values.put(argName, value);
				}
				efd.setValues(values);
			}
			realizedFlow.setFlowDescriptor(efd);
			// new Thread("SLC Flow " + name + " from Eclipse command "
			// + command.getId()) {
			// public void run() {
			modulesManager.start(realizedFlow.getModuleNameVersion());
			modulesManager.execute(realizedFlow);
			// }
			// }.start();
			return null;
		} catch (Exception e) {
			throw new SlcException("Could not execute command "
					+ event.getCommand() + " as SLC flow", e);
		}
	}

	public void setModulesManager(
			ExecutionModulesManager executionModulesManager) {
		this.modulesManager = executionModulesManager;
	}

}
