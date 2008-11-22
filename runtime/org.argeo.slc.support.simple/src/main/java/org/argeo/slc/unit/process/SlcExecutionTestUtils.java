package org.argeo.slc.unit.process;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.argeo.slc.unit.UnitUtils.assertDateSec;

import java.util.UUID;

import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.process.SlcExecutionStep;

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
		assertEquals(expected.logAsString(), reached.logAsString());
		assertDateSec(expected.getBegin(), reached.getBegin());
	}

	private SlcExecutionTestUtils() {

	}
}
