package org.argeo.slc.core.process;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.argeo.slc.core.xml.XmlUtils;
import org.argeo.slc.unit.AbstractSpringTestCase;

public class SlcExecutionCastorTest extends AbstractSpringTestCase {
	private Log log = LogFactory.getLog(getClass());

	public void testMarshalling() throws Exception {
		Marshaller marshaller = getBean("castorMarshaller");

		SlcExecution exec1 = new SlcExecution();
		exec1.setUuid(UUID.randomUUID().toString());
		exec1.setHost("localhost");
		exec1.setPath("/test");
		exec1.setType("slcAnt");
		exec1.setStatus("STARTED");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		SlcExecutionStep step1 = new SlcExecutionStep();
		step1.setBegin(sdf.parse("2008-04-17 18:21"));
		step1.setType("LOG");
		step1.setLog("A log message\nand another line");
		step1.setSlcExecution(exec1);

		Message msg1 = new Message();
		msg1.addPart(exec1);
		msg1.addPart(step1);

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder parser = factory.newDocumentBuilder();
		Document doc = parser.newDocument();
		// Element rootElem = doc.createElement("msg");
		// doc.appendChild(rootElem);
		DOMResult domResult = new DOMResult(doc);

		marshaller.marshal(msg1, domResult);
		// marshaller.marshal(step1, domResult);
		// marshaller.marshal(exec1, domResult);

		String xml = XmlUtils.getDomAsString(doc, true);
		log.info(xml);

		Unmarshaller unmarshaller = getBean("castorMarshaller");

		StringReader reader = new StringReader(xml);
		Document parsedDoc = parser.parse(new InputSource(reader));

		Message message = (Message) unmarshaller.unmarshal(new DOMSource(
				parsedDoc));

		assertNotNull(message);

		for (Object obj : message.getParts()) {
			if (obj instanceof SlcExecutionStep) {
				assertSlcExecution(exec1, ((SlcExecutionStep) obj)
						.getSlcExecution());
				log.debug("Execution step ok");
			}
		}

		log.info(message.getParts());

		// NodeList lstSteps = parsedDoc
		// .getElementsByTagName("slc-execution-step");
		// SlcExecutionStep slcExecutionStep = (SlcExecutionStep) unmarshaller
		// .unmarshal(new DOMSource(lstSteps.item(0)));

		// assertNotNull(slcExecutionStep);
		//
		// SlcExecution slcExecution = slcExecutionStep.getSlcExecution();
		//
		// assertNotNull(slcExecution);
		// assertEquals(exec1.getHost(), slcExecution.getHost());
		// assertEquals(exec1.getPath(), slcExecution.getPath());
		// assertEquals(exec1.getType(), slcExecution.getType());
		// assertEquals(exec1.getStatus(), slcExecution.getStatus());

	}

	private void assertSlcExecution(SlcExecution expected, SlcExecution reached) {
		assertNotNull(reached);
		assertEquals(expected.getHost(), reached.getHost());
		assertEquals(expected.getPath(), reached.getPath());
		assertEquals(expected.getType(), reached.getType());
		assertEquals(expected.getStatus(), reached.getStatus());
	}
}
