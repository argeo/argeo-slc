package org.argeo.slc.core.runtime;

import java.util.List;

import org.argeo.slc.runtime.SlcAgent;
import org.argeo.slc.runtime.SlcAgentFactory;

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
