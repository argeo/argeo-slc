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
package org.argeo.slc.core.runtime;

import java.util.List;

import org.argeo.slc.runtime.SlcAgent;
import org.argeo.slc.runtime.SlcAgentFactory;

/** @deprecated old prototype, should be removed */
public class SimpleAgentFactory implements SlcAgentFactory {
	private List<SlcAgent> agents;

	public SlcAgent getAgent(String uuid) {
		for (SlcAgent agent : agents)
			if (agent.getAgentUuid().equals(uuid))
				return agent;
		return null;
	}

	public void pingAll(List<String> activeAgentIds) {
		// do nothing
	}

	public void setAgents(List<SlcAgent> agents) {
		this.agents = agents;
	}

}
