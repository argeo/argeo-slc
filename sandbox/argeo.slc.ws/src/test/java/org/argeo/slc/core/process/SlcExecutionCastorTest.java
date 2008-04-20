package org.argeo.slc.core.process;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.UUID;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.msg.process.SlcExecutionRequest;
import org.argeo.slc.msg.process.SlcExecutionStepsRequest;
import org.argeo.slc.unit.AbstractSpringTestCase;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;

public class SlcExecutionCastorTest extends AbstractSpringTestCase {
	private Log log = LogFactory.getLog(getClass());

	public void testMarshalling() throws Exception {
		Marshaller marshaller = getBean("marshaller");
		Unmarshaller unmarshaller = getBean("marshaller");

		SlcExecution slcExec = createSimpleSlcExecution();

		SlcExecutionRequest msgSave = new SlcExecutionRequest();
		msgSave.setSlcExecution(slcExec);

		String msgSaveXml = marshallAndLog(marshaller, msgSave);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		SlcExecutionStep step0 = new SlcExecutionStep();
		step0.setBegin(sdf.parse("2008-04-17 18:21"));
		step0.setType("LOG");
		step0.addLog("A log message\nand another line");

		SlcExecutionStep step1 = new SlcExecutionStep();
		step1.setBegin(sdf.parse("2008-04-17 18:25"));
		step1.setType("LOG");
		step1.addLog("A nothe rlog message");

		SlcExecutionStepsRequest msgNotif = new SlcExecutionStepsRequest();
		msgNotif.addStep(step0);
		msgNotif.addStep(step1);
		msgNotif.setSlcExecutionUuid(slcExec.getUuid());

		String msgNotifXml = marshallAndLog(marshaller, msgNotif);

		SlcExecutionRequest msgSaveUnm = unmarshall(unmarshaller, msgSaveXml);
		assertNotNull(msgSaveUnm);
		assertSlcExecution(slcExec, msgSaveUnm.getSlcExecution());

		SlcExecutionStepsRequest msgNotifUnm = unmarshall(unmarshaller,
				msgNotifXml);
		assertNotNull(msgNotifUnm);
		assertEquals(slcExec.getUuid(), msgNotifUnm.getSlcExecutionUuid());
		assertEquals(2, msgNotifUnm.getSteps().size());
		assertSlcExecutionStep(step0, msgNotifUnm.getSteps().get(0));
		assertSlcExecutionStep(step1, msgNotifUnm.getSteps().get(1));

		SlcExecution slcExecUnm = msgSaveUnm.getSlcExecution();
		slcExecUnm.getSteps().addAll(msgNotifUnm.getSteps());

		SlcExecutionRequest msgUpdate = new SlcExecutionRequest();
		msgUpdate.setSlcExecution(slcExecUnm);
		String msgUpdateXml = marshallAndLog(marshaller, msgUpdate);
	}

	private String marshallAndLog(Marshaller marshaller, Object obj)
			throws IOException {
		StringWriter writer = new StringWriter();
		marshaller.marshal(obj, new StreamResult(writer));
		String xml = writer.toString();
		log.info(xml);
		IOUtils.closeQuietly(writer);
		return xml;
	}

	private <T> T unmarshall(Unmarshaller unmarshaller, String xml)
			throws IOException {
		StringReader reader = new StringReader(xml);
		Object obj = unmarshaller.unmarshal(new StreamSource(reader));
		IOUtils.closeQuietly(reader);
		return (T) obj;
	}

	private void assertSlcExecution(SlcExecution expected, SlcExecution reached) {
		assertNotNull(reached);
		assertEquals(expected.getHost(), reached.getHost());
		assertEquals(expected.getPath(), reached.getPath());
		assertEquals(expected.getType(), reached.getType());
		assertEquals(expected.getStatus(), reached.getStatus());
	}

	private void assertSlcExecutionStep(SlcExecutionStep expected,
			SlcExecutionStep reached) {
		assertNotNull(reached);
		assertEquals(expected.getType(), reached.getType());
		assertEquals(expected.logAsString(), reached.logAsString());
		assertEquals(expected.getBegin(), reached.getBegin());
	}

	public static SlcExecution createSimpleSlcExecution() {
		SlcExecution slcExec = new SlcExecution();
		slcExec.setUuid(UUID.randomUUID().toString());
		slcExec.setHost("localhost");
		slcExec.setPath("/test");
		slcExec.setType("slcAnt");
		slcExec.setStatus("STARTED");
		return slcExec;
	}
}
