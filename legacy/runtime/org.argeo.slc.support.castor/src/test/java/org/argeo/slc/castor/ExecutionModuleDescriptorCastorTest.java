/*
 * Copyright (C) 2007-2012 Mathieu Baudier
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
		moduleDescriptor.setName("test.module");
		moduleDescriptor.setVersion("1.0.0");
		moduleDescriptor.setLabel("Test Module");
		moduleDescriptor.setDescription("module descriptor");

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

	public void testMarshUnmarshMini() throws Exception {
		ExecutionModuleDescriptor moduleDescriptor = new ExecutionModuleDescriptor();
		moduleDescriptor.setName("test.moodule");
		moduleDescriptor.setVersion("1.0.0");
		marshUnmarsh(moduleDescriptor, false);
	}

}