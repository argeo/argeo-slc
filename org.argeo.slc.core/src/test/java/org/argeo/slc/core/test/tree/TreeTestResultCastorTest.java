package org.argeo.slc.core.test.tree;

import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.xml.transform.StringResult;
import org.springframework.xml.transform.StringSource;
import org.springframework.xml.xsd.XsdSchema;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static org.argeo.slc.core.test.tree.TreeTestResultTestUtils.createCompleteTreeTestResult;

import org.argeo.slc.msg.test.tree.TreeTestResultRequest;
import org.argeo.slc.unit.AbstractSpringTestCase;
import org.argeo.slc.unit.UnitXmlUtils;
import org.argeo.slc.unit.test.tree.UnitTestTreeUtil;

public class TreeTestResultCastorTest extends AbstractSpringTestCase {
	private Log log = LogFactory.getLog(getClass());
	
	private Marshaller marshaller;
	private Unmarshaller unmarshaller;

	@Override
	public void setUp() {
		marshaller = getBean("marshaller");
		unmarshaller = getBean("marshaller");
	}



	public void testMarshUnmarsh() throws Exception {
		TreeTestResult ttr = createCompleteTreeTestResult();

		StringResult xml = new StringResult();
		marshaller.marshal(ttr, xml);

		log.info("Marshalled TreeTestResult: " + xml);

		XsdSchema schema = getBean("schema");
		UnitXmlUtils.assertXsdSchemaValidation(schema, new StringSource(xml
				.toString()));

		TreeTestResult ttrUnm = (TreeTestResult) unmarshaller
				.unmarshal(new StringSource(xml.toString()));

		UnitTestTreeUtil.assertTreeTestResult(ttr, ttrUnm);
	}
	
	public void testTreeTestResultRequest() throws Exception{
		TreeTestResultRequest req = new TreeTestResultRequest();
		req.setTreeTestResult(createCompleteTreeTestResult());
		
		StringResult xml = new StringResult();
		marshaller.marshal(req, xml);

		log.info("Marshalled TreeTestResult Request: " + xml);

		XsdSchema schema = getBean("schema");
		UnitXmlUtils.assertXsdSchemaValidation(schema, new StringSource(xml
				.toString()));

		TreeTestResultRequest reqUnm = (TreeTestResultRequest) unmarshaller
				.unmarshal(new StringSource(xml.toString()));

		UnitTestTreeUtil.assertTreeTestResult(req.getTreeTestResult(), reqUnm.getTreeTestResult());
	}
}
