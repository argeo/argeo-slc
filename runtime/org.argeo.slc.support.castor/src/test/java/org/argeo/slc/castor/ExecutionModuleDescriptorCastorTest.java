package org.argeo.slc.castor;

import java.util.ArrayList;
import java.util.List;

import org.argeo.slc.execution.ExecutionFlowDescriptor;
import org.argeo.slc.execution.ExecutionModuleDescriptor;
import org.argeo.slc.execution.ExecutionSpec;
import org.argeo.slc.unit.execution.ExecutionFlowDescriptorTestUtils;

public class ExecutionModuleDescriptorCastorTest extends AbstractCastorTestCase {
	public void testMarshUnmarsh() throws Exception {
		ExecutionModuleDescriptor moduleDescriptor = new ExecutionModuleDescriptor();
		moduleDescriptor.setName("test.moodule");
		moduleDescriptor.setVersion("1.0.0");

		ExecutionFlowDescriptor flowDescriptor = ExecutionFlowDescriptorTestUtils
				.createSimpleExecutionFlowDescriptor();

		List<ExecutionFlowDescriptor> flows = new ArrayList<ExecutionFlowDescriptor>();
		flows.add(flowDescriptor);
		moduleDescriptor.setExecutionFlows(flows);

		List<ExecutionSpec> specs = new ArrayList<ExecutionSpec>();
		specs.add(flowDescriptor.getExecutionSpec());
		moduleDescriptor.setExecutionSpecs(specs);

		marshUnmarsh(moduleDescriptor, false);
	}
}
