package org.argeo.slc.it.webapp;

import org.argeo.slc.msg.ObjectList;
import org.argeo.slc.server.client.SlcServerHttpClient;
import org.argeo.slc.server.unit.AbstractHttpClientTestCase;

public class AgentTest extends AbstractHttpClientTestCase{
	public void testListAgents() throws Exception {
		waitForServerToBeReady();
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
