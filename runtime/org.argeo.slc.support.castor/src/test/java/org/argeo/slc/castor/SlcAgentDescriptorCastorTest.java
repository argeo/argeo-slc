package org.argeo.slc.castor;

import java.util.ArrayList;
import java.util.List;

import org.argeo.slc.execution.ExecutionFlowDescriptor;
import org.argeo.slc.execution.ExecutionModuleDescriptor;
import org.argeo.slc.execution.ExecutionSpec;
import org.argeo.slc.msg.ObjectList;
import org.argeo.slc.runtime.SlcAgentDescriptor;
import org.argeo.slc.unit.execution.ExecutionFlowDescriptorTestUtils;

public class SlcAgentDescriptorCastorTest extends AbstractCastorTestCase {
	public void testMarshUnmarshMini() throws Exception {
		SlcAgentDescriptor agentDescriptor = createMiniAgentDescriptor();
		marshUnmarsh(agentDescriptor, false);
	}

	public void testMarshUnmarshWithModuleDescriptor() throws Exception {
		SlcAgentDescriptor agentDescriptor = createMiniAgentDescriptor();

		List<ExecutionModuleDescriptor> lst = new ArrayList<ExecutionModuleDescriptor>();
		ExecutionModuleDescriptor moduleDescriptor = new ExecutionModuleDescriptor();
		moduleDescriptor.setName("test.moodule");
		moduleDescriptor.setVersion("1.0.0");
		lst.add(moduleDescriptor);
		agentDescriptor.setModuleDescriptors(lst);

		marshUnmarsh(agentDescriptor, false);
	}
	
	protected static SlcAgentDescriptor createMiniAgentDescriptor(){
		SlcAgentDescriptor agentDescriptor = new SlcAgentDescriptor();
		agentDescriptor.setHost("localhost");
		agentDescriptor.setUuid("555");
		return agentDescriptor;
	}
}
