package org.argeo.slc.core.process;

import java.io.IOException;
import java.io.InputStream;
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
import org.argeo.slc.unit.UnitXmlUtils;

import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.xml.transform.StringResult;
import org.springframework.xml.transform.StringSource;
import org.springframework.xml.xsd.XsdSchema;

public class SlcExecutionCastorTest extends AbstractSpringTestCase {
	private Log log = LogFactory.getLog(getClass());

	private Marshaller marshaller;
	private Unmarshaller unmarshaller;

	@Override
	public void setUp() {
		marshaller = getBean("marshaller");
		unmarshaller = getBean("marshaller");
	}

	public void testMarshalling() throws Exception {
		SlcExecution slcExec = SlcExecutionTestUtils.createSimpleSlcExecution();

		SlcExecutionRequest msgSave = new SlcExecutionRequest();
		msgSave.setSlcExecution(slcExec);

		String msgSaveXml = marshallAndLog(marshaller, msgSave);

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

		String msgNotifXml = marshallAndLog(marshaller, msgNotif);

		SlcExecutionRequest msgSaveUnm = unmarshall(unmarshaller, msgSaveXml);
		assertNotNull(msgSaveUnm);
		SlcExecutionTestUtils.assertSlcExecution(slcExec, msgSaveUnm
				.getSlcExecution());

		SlcExecutionStepsRequest msgNotifUnm = unmarshall(unmarshaller,
				msgNotifXml);
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
		String msgUpdateXml = marshallAndLog(marshaller, msgUpdate);

		SlcExecutionRequest msgUpdateUnm = unmarshall(unmarshaller,
				msgUpdateXml);
		assertNotNull(msgUpdateUnm);
	}

	private String marshallAndLog(Marshaller marshaller, Object obj)
			throws IOException {
		StringResult xml = new StringResult();
		marshaller.marshal(obj, xml);
		log.info("Marshalled object: " + xml);

		XsdSchema schema = getBean("schema");
		UnitXmlUtils.assertXsdSchemaValidation(schema, new StringSource(xml
				.toString()));

		return xml.toString();
	}

	private <T> T unmarshall(Unmarshaller unmarshaller, String xml)
			throws IOException {
		StringReader reader = new StringReader(xml);
		Object obj = unmarshaller.unmarshal(new StreamSource(reader));
		IOUtils.closeQuietly(reader);
		return (T) obj;
	}
}
