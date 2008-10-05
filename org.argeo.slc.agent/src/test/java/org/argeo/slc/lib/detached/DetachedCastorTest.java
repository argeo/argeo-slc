package org.argeo.slc.lib.detached;

import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.detached.DetachedAnswer;
import org.argeo.slc.detached.DetachedRequest;
import org.argeo.slc.detached.DetachedXmlConverter;
import org.argeo.slc.detached.DetachedXmlConverterCompat;
import org.argeo.slc.unit.AbstractSpringTestCase;
import org.argeo.slc.unit.UnitXmlUtils;
import org.springframework.xml.transform.StringResult;
import org.springframework.xml.transform.StringSource;
import org.springframework.xml.validation.XmlValidator;

public class DetachedCastorTest extends AbstractSpringTestCase {
	private final Log log = LogFactory.getLog(getClass());

	private DetachedXmlConverter converter = null;
	private XmlValidator validator = null;
	private DetachedXmlConverter converterCompat = null;

	public void setUp() {
		converter = getBean("slcDefault.detached.xmlConverterCastor");
		validator = getBean("slcDefault.detached.xml.xsdValidator");
		converterCompat = new DetachedXmlConverterCompat();
	}

	public void testRequest() throws Exception {
		DetachedRequest request = createTestRequest();

		StringResult result = new StringResult();
		converter.marshallCommunication(request, result);
		String xml = result.toString();
		log.debug(xml + "\n");

		UnitXmlUtils.assertXmlValidation(validator, new StringSource(xml));
		DetachedRequest requestUnm = (DetachedRequest) converter
				.unmarshallCommunication(new StringSource(xml));
		assertDetachedRequest(request, requestUnm);
	}

	public void testRequestCompat() throws Exception {
		DetachedRequest request = createTestRequest();

		StringResult result = new StringResult();
		converter.marshallCommunication(request, result);
		String xml = result.toString();
		log.debug(xml + "\n");

		UnitXmlUtils.assertXmlValidation(validator, new StringSource(xml));
		DetachedRequest requestUnm = (DetachedRequest) converterCompat
				.unmarshallCommunication(new StringSource(xml));
		assertDetachedRequest(request, requestUnm);
	}

	public void testAnswer() throws Exception {
		DetachedAnswer answer = createTestAnswer();
		StringResult result = new StringResult();
		converter.marshallCommunication(answer, result);
		String xml = result.toString();
		log.debug(xml + "\n");

		UnitXmlUtils.assertXmlValidation(validator, new StringSource(xml));
		DetachedAnswer answerUnm = (DetachedAnswer) converter
				.unmarshallCommunication(new StringSource(xml));
		assertDetachedAnswer(answer, answerUnm);
	}

	public void testAnswerCompat() throws Exception {
		DetachedAnswer answer = createTestAnswer();
		StringResult result = new StringResult();
		converterCompat.marshallCommunication(answer, result);
		String xml = result.toString();
		log.debug(xml + "\n");

		UnitXmlUtils.assertXmlValidation(validator, new StringSource(xml));
		DetachedAnswer answerUnm = (DetachedAnswer) converter
				.unmarshallCommunication(new StringSource(xml));
		assertDetachedAnswer(answer, answerUnm);
	}

	public static DetachedRequest createTestRequest() {
		DetachedRequest request = new DetachedRequest();
		request.setUuid("12345");
		request.setPath("/root/test");
		request.setRef("testRef");
		Properties properties = new Properties();
		properties.setProperty("key1", "value1");
		properties.setProperty("key2", "value2");
		request.setProperties(properties);
		return request;
	}

	public static DetachedAnswer createTestAnswer() {
		DetachedAnswer answer = new DetachedAnswer();
		answer.setUuid("12345");
		answer.setStatus(DetachedAnswer.PROCESSED);
		answer.setLog("my log\nline break.");
		Properties properties = new Properties();
		properties.setProperty("key1", "value1");
		properties.setProperty("key2", "value2");
		answer.setProperties(properties);
		return answer;
	}

	public static void assertDetachedRequest(DetachedRequest expected,
			DetachedRequest reached) {
		assertEquals(expected.getUuid(), reached.getUuid());
		assertEquals(expected.getPath(), reached.getPath());
		assertEquals(expected.getRef(), reached.getRef());
		Properties expectedProps = expected.getProperties();
		Properties reachedProps = reached.getProperties();
		assertEquals(expectedProps.size(), reachedProps.size());
		Enumeration<Object> keys = expectedProps.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			assertTrue(reachedProps.containsKey(key));
			assertEquals(expectedProps.getProperty(key), reachedProps
					.getProperty(key));
		}
	}

	public static void assertDetachedAnswer(DetachedAnswer expected,
			DetachedAnswer reached) {
		assertEquals(expected.getUuid(), reached.getUuid());
		assertEquals(expected.getStatus(), reached.getStatus());
		assertEquals(expected.getLog(), reached.getLog());
		Properties expectedProps = expected.getProperties();
		Properties reachedProps = reached.getProperties();
		assertEquals(expectedProps.size(), reachedProps.size());
		Enumeration<Object> keys = expectedProps.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			assertTrue(reachedProps.containsKey(key));
			assertEquals(expectedProps.getProperty(key), reachedProps
					.getProperty(key));
		}
	}
}
