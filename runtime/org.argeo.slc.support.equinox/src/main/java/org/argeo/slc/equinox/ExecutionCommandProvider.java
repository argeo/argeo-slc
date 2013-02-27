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
package org.argeo.slc.equinox;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.execution.RealizedFlow;
import org.argeo.slc.osgi.OsgiExecutionModulesManager;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

public class ExecutionCommandProvider implements CommandProvider {
	private final static Log log = LogFactory
			.getLog(ExecutionCommandProvider.class);

	private final static String SLC_WITH_REFRESH = "slc";
	private final static String SLC_NO_REFRESH = "slcnr";

	private OsgiExecutionModulesManager modulesManager;

	private RealizedFlow lastLaunch = null;

	public Object _slc(CommandInterpreter ci) {
		return exec(SLC_WITH_REFRESH, ci);
	}

	public Object _slcnr(CommandInterpreter ci) {
		return exec(SLC_NO_REFRESH, ci);
	}

	protected Object exec(String slcCommand, CommandInterpreter ci) {
		// TODO: check version
		String firstArg = ci.nextArgument();
		if (firstArg == null) {
			if (lastLaunch != null) {
				String cmd = slcCommand + " " + lastLaunch.getModuleName()
						+ " " + lastLaunch.getFlowDescriptor().getName();
				if (log.isDebugEnabled())
					log.debug("Execute again last command: " + cmd);
				return ci.execute(cmd);
			} else {
				ci.execute("help");
				throw new SlcException("Command not properly formatted");
			}
		}
		String executionName = ci.nextArgument();
		

		launch(slcCommand, firstArg, executionName);
		return "COMMAND COMPLETED";
	}

	protected void launch(String slcCommand, String firstArg,
			String executionName) {
		lastLaunch = modulesManager.findRealizedFlow(firstArg, executionName);
		if (lastLaunch == null)
			throw new SlcException("Cannot find launch for " + firstArg + " "
					+ executionName);

		// Execute
		if (SLC_WITH_REFRESH.equals(slcCommand)) {
			modulesManager.upgrade(lastLaunch.getModuleNameVersion());
			modulesManager.execute(lastLaunch);
		} else if (SLC_NO_REFRESH.equals(slcCommand))
			modulesManager.execute(lastLaunch);
		else
			throw new SlcException("Unrecognized SLC command " + slcCommand);
	}

	public String getHelp() {
		StringBuffer buf = new StringBuffer();
		buf.append("---SLC Execution Commands---\n");
		buf
				.append("\tslc (<id>|<segment of bsn>) <execution bean>"
						+ "  - refresh the bundle, execute an execution flow (without arg, execute last)\n");
		buf
				.append("\tslcnr (<id>|<segment of bsn>) <execution bean>"
						+ "  - execute an execution flow (without arg, execute last)\n");
		return buf.toString();

	}

	public void setModulesManager(OsgiExecutionModulesManager osgiModulesManager) {
		this.modulesManager = osgiModulesManager;
	}

}
