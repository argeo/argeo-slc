package org.argeo.slc.it.webapp;

import java.net.InetAddress;

import org.argeo.slc.msg.ObjectList;
import org.argeo.slc.runtime.SlcAgentDescriptor;
import org.argeo.slc.server.client.SlcServerHttpClient;
import org.argeo.slc.unit.AbstractSpringTestCase;

public class AgentTest extends AbstractSpringTestCase {
	public void testListAgents() throws Exception {
		SlcServerHttpClient httpClient = getBean(SlcServerHttpClient.class);
		ObjectList objectList = httpClient.callService("listAgents.service",
				null);
		assertEquals(0, objectList.getObjects().size());
//		SlcAgentDescriptor slcAgentDescriptor = (SlcAgentDescriptor) objectList
//				.getObjects().get(0);
//		assertEquals(InetAddress.getLocalHost().getHostName(),
//				slcAgentDescriptor.getHost());
	}

}
