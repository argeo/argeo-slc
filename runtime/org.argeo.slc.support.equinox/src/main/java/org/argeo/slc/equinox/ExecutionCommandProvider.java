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

import java.util.ArrayList;
import java.util.List;

import org.argeo.slc.SlcException;
import org.argeo.slc.execution.SlcAgentCli;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

public class ExecutionCommandProvider implements CommandProvider {
	private SlcAgentCli agentCli;

	private String lastProcessUuid;

	public Object _slc(CommandInterpreter ci) {
		List<String> args = new ArrayList<String>();
		String arg = null;
		while ((arg = ci.nextArgument()) != null)
			args.add(arg);
		if (args.size() == 0) {
			// TODO relaunch last process
			ci.execute("help");
			throw new SlcException("Command not properly formatted");
		}

		lastProcessUuid = agentCli
				.process(args.toArray(new String[args.size()]));
		return lastProcessUuid;
	}

	public String getHelp() {
		StringBuffer buf = new StringBuffer();
		buf.append("---SLC Execution Commands---\n");
		buf.append("\tslc <module> <flow> [[ --arg value | --booleanArg ]]"
				+ "  - executes an execution flow\n");
		return buf.toString();

	}

	public void setAgentCli(SlcAgentCli agentCli) {
		this.agentCli = agentCli;
	}

}
