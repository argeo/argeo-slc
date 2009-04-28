package org.argeo.slc.runtime;

import java.util.List;

public interface SlcAgentFactory {
	public SlcAgent getAgent(String uuid);

	public void pingAll(List<String> activeAgentIds);
}
