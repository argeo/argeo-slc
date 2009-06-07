package org.argeo.slc.castor;

import org.argeo.slc.msg.ObjectList;
import org.argeo.slc.runtime.SlcAgentDescriptor;

public class ObjectListCastorTest extends AbstractCastorTestCase {

	public void testAgentDescriptorList() throws Exception {
		SlcAgentDescriptor agentDescriptor = SlcAgentDescriptorCastorTest
				.createMiniAgentDescriptor();
		ObjectList lst = new ObjectList();
		lst.getObjects().add(agentDescriptor);
		ObjectList lstUnm = (ObjectList) marshUnmarsh(lst, false);
		assertEquals(1, lstUnm.getObjects().size());
	}
}
