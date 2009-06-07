package org.argeo.slc.it.webapp;

import java.net.InetAddress;

import org.argeo.slc.runtime.SlcAgentDescriptor;
import org.argeo.slc.server.unit.AbstractHttpClientTestCase;

public class AgentTest extends AbstractHttpClientTestCase {
	public void testListAgents() throws Exception {
		SlcAgentDescriptor slcAgentDescriptor = getHttpClient()
				.waitForOneAgent();
		assertEquals(InetAddress.getLocalHost().getHostName(),
				slcAgentDescriptor.getHost());
	}

}
