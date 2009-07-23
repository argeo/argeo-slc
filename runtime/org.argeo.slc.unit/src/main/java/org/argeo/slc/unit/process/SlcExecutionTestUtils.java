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
	}

	public static void assertSlcExecutionStep(SlcExecutionStep expected,
			SlcExecutionStep reached) {
		assertNotNull(reached);
		assertEquals(expected.getUuid(), reached.getUuid());
		assertEquals(expected.getType(), reached.getType());
		assertDateSec(expected.getBegin(), reached.getBegin());
	}

	private SlcExecutionTestUtils() {

	}
}
