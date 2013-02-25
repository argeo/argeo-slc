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
package org.argeo.slc.core.execution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.execution.SlcAgent;
import org.argeo.slc.execution.SlcAgentFactory;

/** Register agents (typically via OSGi listeners) */
public class DefaultAgentFactory implements SlcAgentFactory {
	private final static Log log = LogFactory.getLog(DefaultAgentFactory.class);

	private Map<String, SlcAgent> agents = new HashMap<String, SlcAgent>();

	public SlcAgent getAgent(String uuid) {
		if (agents.containsKey(uuid))
			return agents.get(uuid);
		else
			return null;
	}

	public void pingAll(List<String> activeAgentIds) {
		for (SlcAgent agent : agents.values())
			agent.ping();
	}

	public synchronized void register(SlcAgent agent,
			Map<String, String> properties) {
		agents.put(agent.getAgentUuid(), agent);
		if (log.isDebugEnabled())
			log.debug("Agent " + agent.getAgentUuid() + " registered");
	}

	public synchronized void unregister(SlcAgent agent,
			Map<String, String> properties) {
		agents.remove(agent.getAgentUuid());
		if (log.isDebugEnabled())
			log.debug("Agent " + agent.getAgentUuid() + " unregistered");
	}

}
