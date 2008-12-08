package org.argeo.slc.castor;

import java.text.SimpleDateFormat;
import java.util.UUID;

import org.argeo.slc.msg.process.SlcExecutionRequest;
import org.argeo.slc.msg.process.SlcExecutionStepsRequest;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.process.SlcExecutionStep;
import org.argeo.slc.unit.process.SlcExecutionTestUtils;
import org.springframework.xml.transform.StringResult;

public class SlcExecutionCastorTest extends AbstractCastorTestCase {
	public void testMarshalling() throws Exception {
		SlcExecution slcExec = SlcExecutionTestUtils.createSimpleSlcExecution();

		SlcExecutionRequest msgSave = new SlcExecutionRequest();
		msgSave.setSlcExecution(slcExec);

		StringResult msgSaveXml = marshalAndValidate(msgSave);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		SlcExecutionStep step0 = new SlcExecutionStep();
		step0.setUuid(UUID.randomUUID().toString());
		step0.setBegin(sdf.parse("2008-04-17 18:21"));
		step0.setType("LOG");
		step0.addLog("A log message\nand another line");

		SlcExecutionStep step1 = new SlcExecutionStep();
		step1.setUuid(UUID.randomUUID().toString());
		step1.setBegin(sdf.parse("2008-04-17 18:25"));
		step1.setType("LOG");
		step1.addLog("A nother log message");

		SlcExecutionStepsRequest msgNotif = new SlcExecutionStepsRequest();
		msgNotif.addStep(step0);
		msgNotif.addStep(step1);
		msgNotif.setSlcExecutionUuid(slcExec.getUuid());

		StringResult msgNotifXml = marshalAndValidate(msgNotif);

		SlcExecutionRequest msgSaveUnm = unmarshal(msgSaveXml);
		assertNotNull(msgSaveUnm);
		SlcExecutionTestUtils.assertSlcExecution(slcExec, msgSaveUnm
				.getSlcExecution());

		SlcExecutionStepsRequest msgNotifUnm = unmarshal(msgNotifXml);
		assertNotNull(msgNotifUnm);
		assertEquals(slcExec.getUuid(), msgNotifUnm.getSlcExecutionUuid());
		assertEquals(2, msgNotifUnm.getSteps().size());
		SlcExecutionTestUtils.assertSlcExecutionStep(step0, msgNotifUnm
				.getSteps().get(0));
		SlcExecutionTestUtils.assertSlcExecutionStep(step1, msgNotifUnm
				.getSteps().get(1));

		SlcExecution slcExecUnm = msgSaveUnm.getSlcExecution();
		slcExecUnm.getSteps().addAll(msgNotifUnm.getSteps());

		SlcExecutionRequest msgUpdate = new SlcExecutionRequest();
		msgUpdate.setSlcExecution(slcExecUnm);
		StringResult msgUpdateXml = marshalAndValidate(msgUpdate);

		SlcExecutionRequest msgUpdateUnm = unmarshal(msgUpdateXml);
		assertNotNull(msgUpdateUnm);
	}
}
