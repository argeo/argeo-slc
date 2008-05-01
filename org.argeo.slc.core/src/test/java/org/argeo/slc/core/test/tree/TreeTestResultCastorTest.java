package org.argeo.slc.core.test.tree;

import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.xml.transform.StringResult;
import org.springframework.xml.transform.StringSource;
import org.springframework.xml.validation.XmlValidator;
import org.springframework.xml.xsd.XsdSchema;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static org.argeo.slc.core.test.tree.TreeTestResultTestUtils.createCompleteTreeTestResult;

import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.TestResultPart;
import org.argeo.slc.msg.test.tree.CreateTreeTestResultRequest;
import org.argeo.slc.msg.test.tree.ResultPartRequest;
import org.argeo.slc.unit.AbstractSpringTestCase;
import org.argeo.slc.unit.UnitXmlUtils;
import org.argeo.slc.unit.test.tree.UnitTestTreeUtil;

public class TreeTestResultCastorTest extends AbstractSpringTestCase {
	private Log log = LogFactory.getLog(getClass());

	private Marshaller marshaller;
	private Unmarshaller unmarshaller;

	@Override
	public void setUp() {
		marshaller = getBean(Marshaller.class);
		unmarshaller = getBean(Unmarshaller.class);
	}

	public void testMarshUnmarsh() throws Exception {
		TreeTestResult ttr = createCompleteTreeTestResult();

		StringResult xml = new StringResult();
		marshaller.marshal(ttr, xml);

		log.info("Marshalled TreeTestResult: " + xml);

		UnitXmlUtils.assertXmlValidation(getBean(XmlValidator.class),
				new StringSource(xml.toString()));

		TreeTestResult ttrUnm = (TreeTestResult) unmarshaller
				.unmarshal(new StringSource(xml.toString()));

		UnitTestTreeUtil.assertTreeTestResult(ttr, ttrUnm);
	}

	public void testCreateTreeTestResultRequest() throws Exception {
		CreateTreeTestResultRequest req = new CreateTreeTestResultRequest();
		req.setTreeTestResult(createCompleteTreeTestResult());

		StringResult xml = new StringResult();
		marshaller.marshal(req, xml);

		log.info("Marshalled CreateTreeTestResult Request: " + xml);

		UnitXmlUtils.assertXmlValidation(getBean(XmlValidator.class),
				new StringSource(xml.toString()));

		CreateTreeTestResultRequest reqUnm = (CreateTreeTestResultRequest) unmarshaller
				.unmarshal(new StringSource(xml.toString()));

		UnitTestTreeUtil.assertTreeTestResult(req.getTreeTestResult(), reqUnm
				.getTreeTestResult());
	}

	public void testResultPartRequest() throws Exception {
		TreeTestResult ttr = createCompleteTreeTestResult();

		TreeSPath path = ttr.getCurrentPath();
		PartSubList lst = ttr.getResultParts().get(path);
		//TestResultPart part = lst.getParts().get(lst.getParts().size() - 1);
		TestResultPart part = lst.getParts().get(2);

		ResultPartRequest req = new ResultPartRequest(ttr, path, part);
		req.setPath(ttr.getCurrentPath());

		StringResult xml = new StringResult();
		marshaller.marshal(req, xml);

		log.info("Marshalled ResultPart Request: " + xml);

		UnitXmlUtils.assertXmlValidation(getBean(XmlValidator.class),
				new StringSource(xml.toString()));

		ResultPartRequest reqUnm = (ResultPartRequest) unmarshaller
				.unmarshal(new StringSource(xml.toString()));

		UnitTestTreeUtil
				.assertPart(req.getResultPart(), reqUnm.getResultPart());
	}

}
