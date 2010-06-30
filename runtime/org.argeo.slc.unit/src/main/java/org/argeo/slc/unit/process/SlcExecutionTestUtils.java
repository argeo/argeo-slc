/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
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

package org.argeo.slc.unit.process;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.argeo.slc.unit.UnitUtils.assertDateSec;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.argeo.slc.execution.ExecutionFlowDescriptor;
import org.argeo.slc.process.RealizedFlow;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.process.SlcExecutionStep;
import org.argeo.slc.unit.execution.ExecutionFlowDescriptorTestUtils;

public abstract class SlcExecutionTestUtils {

	public static SlcExecution createSimpleSlcExecution() {
		SlcExecution slcExec = new SlcExecution();
		slcExec.setUuid(UUID.randomUUID().toString());
		slcExec.setHost("localhost");
		slcExec.setUser("user");
		slcExec.setType("slcAnt");
		slcExec.setStatus("STARTED");
		slcExec.getAttributes().put("ant.file", "/test");
		return slcExec;
	}

	public static SlcExecution createSlcExecutionWithRealizedFlows() {
		SlcExecution slcExec = SlcExecutionTestUtils.createSimpleSlcExecution();
		List<RealizedFlow> realizedFlows = new ArrayList<RealizedFlow>();
		RealizedFlow realizedFlow = new RealizedFlow();
		ExecutionFlowDescriptor flowDescriptor = ExecutionFlowDescriptorTestUtils
				.createSimpleExecutionFlowDescriptor();
		realizedFlow.setModuleName("test.module");
		realizedFlow.setModuleVersion("1.0.0");
		realizedFlow.setFlowDescriptor(flowDescriptor);
		realizedFlow.setExecutionSpec(flowDescriptor.getExecutionSpec());
		realizedFlows.add(realizedFlow);
		slcExec.setRealizedFlows(realizedFlows);
		return slcExec;
	}

	public static void assertSlcExecution(SlcExecution expected,
			SlcExecution reached) {
		assertNotNull(reached);
		assertEquals(expected.getHost(), reached.getHost());
		assertEquals(expected.getUser(), reached.getUser());
		assertEquals(expected.getType(), reached.getType());
		assertEquals(expected.getStatus(), reached.getStatus());

		// Attributes
		assertEquals(expected.getAttributes().size(), reached.getAttributes()
				.size());
		for (String key : expected.getAttributes().keySet()) {
			String expectedValue = expected.getAttributes().get(key);
			String reachedValue = reached.getAttributes().get(key);
			assertNotNull(reachedValue);
			assertEquals(expectedValue, reachedValue);
		}

		assertEquals(expected.getSteps().size(), reached.getSteps().size());
		for (int i = 0; i < expected.getSteps().size(); i++) {
			SlcExecutionStep stepExpected = expected.getSteps().get(i);
			SlcExecutionStep stepReached = reached.getSteps().get(i);
			assertSlcExecutionStep(stepExpected, stepReached);
		}

		// FIXME: compare realized flows
		// assertEquals(expected.getRealizedFlows().size(), reached
		// .getRealizedFlows().size());

	}

	public static void assertSlcExecutionStep(SlcExecutionStep expected,
			SlcExecutionStep reached) {
		assertNotNull(reached);
		assertEquals(expected.getUuid(), reached.getUuid());
		assertEquals(expected.getType(), reached.getType());
		assertDateSec(expected.getBegin(), reached.getBegin());
		assertEquals(expected.getLogLines().size(), reached.getLogLines()
				.size());
		for (int i = 0; i < expected.getLogLines().size(); i++) {
			assertEquals(expected.getLogLines().get(i), reached.getLogLines()
					.get(i));
		}
	}

	private SlcExecutionTestUtils() {

	}
}
