package org.argeo.slc.castor;

import java.util.ArrayList;
import java.util.List;

import org.argeo.slc.execution.ExecutionModuleDescriptor;
import org.argeo.slc.runtime.SlcAgentDescriptor;

public class SlcAgentDescriptorCastorTest extends AbstractCastorTestCase {
	public void testMarshUnmarshMini() throws Exception {
		SlcAgentDescriptor agentDescriptor = createMiniAgentDescriptor();
		SlcAgentDescriptor agentDescriptorUnm = marshUnmarsh(agentDescriptor,
				false);
		assertSlcAgentDescriptor(agentDescriptor, agentDescriptorUnm);
	}

	public void testMarshUnmarshWithModuleDescriptor() throws Exception {
		SlcAgentDescriptor agentDescriptor = createMiniAgentDescriptor();

		List<ExecutionModuleDescriptor> lst = new ArrayList<ExecutionModuleDescriptor>();
		ExecutionModuleDescriptor moduleDescriptor = new ExecutionModuleDescriptor();
		moduleDescriptor.setName("test.moodule");
		moduleDescriptor.setVersion("1.0.0");
		lst.add(moduleDescriptor);
		agentDescriptor.setModuleDescriptors(lst);

		SlcAgentDescriptor agentDescriptorUnm = marshUnmarsh(agentDescriptor,
				false);
		assertSlcAgentDescriptor(agentDescriptor, agentDescriptorUnm);
	}

	protected static SlcAgentDescriptor createMiniAgentDescriptor() {
		SlcAgentDescriptor agentDescriptor = new SlcAgentDescriptor();
		agentDescriptor.setHost("localhost");
		agentDescriptor.setUuid("555");
		return agentDescriptor;
	}

	protected static void assertSlcAgentDescriptor(SlcAgentDescriptor expected,
			SlcAgentDescriptor reached) {
		assertNotNull(reached);
		assertEquals(expected.getHost(), reached.getHost());
		assertEquals(expected.getUuid(), expected.getUuid());
		assertEquals(expected.getModuleDescriptors().size(), reached
				.getModuleDescriptors().size());
	}
}
