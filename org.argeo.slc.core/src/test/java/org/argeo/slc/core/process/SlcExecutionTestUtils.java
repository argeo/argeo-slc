package org.argeo.slc.core.process;

import java.util.UUID;

import junit.framework.TestCase;

public abstract class SlcExecutionTestUtils extends TestCase{

	public static SlcExecution createSimpleSlcExecution() {
		SlcExecution slcExec = new SlcExecution();
		slcExec.setUuid(UUID.randomUUID().toString());
		slcExec.setHost("localhost");
		slcExec.setPath("/test");
		slcExec.setType("slcAnt");
		slcExec.setStatus("STARTED");
		return slcExec;
	}

	public static void assertSlcExecution(SlcExecution expected, SlcExecution reached) {
		assertNotNull(reached);
		assertEquals(expected.getHost(), reached.getHost());
		assertEquals(expected.getPath(), reached.getPath());
		assertEquals(expected.getType(), reached.getType());
		assertEquals(expected.getStatus(), reached.getStatus());
	}

	public static void assertSlcExecutionStep(SlcExecutionStep expected,
			SlcExecutionStep reached) {
		assertNotNull(reached);
		assertEquals(expected.getUuid(), reached.getUuid());
		assertEquals(expected.getType(), reached.getType());
		assertEquals(expected.logAsString(), reached.logAsString());
		//assertEquals(expected.getBegin(), reached.getBegin());
	}

	private SlcExecutionTestUtils(){
		
	}
}
