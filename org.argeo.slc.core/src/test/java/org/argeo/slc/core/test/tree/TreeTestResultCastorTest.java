package org.argeo.slc.core.test.tree;

import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.xml.transform.StringResult;
import org.springframework.xml.transform.StringSource;
import org.springframework.xml.validation.XmlValidator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static org.argeo.slc.unit.UnitUtils.assertDateSec;
import static org.argeo.slc.unit.test.tree.TreeTestResultTestUtils.createCompleteTreeTestResult;
import static org.argeo.slc.unit.test.tree.TreeTestResultTestUtils.createSimpleResultPartRequest;

import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.SimpleResultPart;
import org.argeo.slc.msg.test.tree.CloseTreeTestResultRequest;
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

		StringResult xml = marshallAndValidate(ttr);

		TreeTestResult ttrUnm = (TreeTestResult) unmarshaller
				.unmarshal(new StringSource(xml.toString()));

		UnitTestTreeUtil.assertTreeTestResult(ttr, ttrUnm);
	}

	public void testCreateTreeTestResultRequest() throws Exception {
		CreateTreeTestResultRequest req = new CreateTreeTestResultRequest();
		req.setTreeTestResult(createCompleteTreeTestResult());

		StringResult xml = marshallAndValidate(req);

		CreateTreeTestResultRequest reqUnm = (CreateTreeTestResultRequest) unmarshaller
				.unmarshal(new StringSource(xml.toString()));

		UnitTestTreeUtil.assertTreeTestResult(req.getTreeTestResult(), reqUnm
				.getTreeTestResult());
	}

	public void testResultPartRequest() throws Exception {
		TreeTestResult ttr = createCompleteTreeTestResult();
		ResultPartRequest req = createSimpleResultPartRequest(ttr);

		StringResult xml = marshallAndValidate(req);

		ResultPartRequest reqUnm = (ResultPartRequest) unmarshaller
				.unmarshal(new StringSource(xml.toString()));

		UnitTestTreeUtil
				.assertPart(req.getResultPart(), reqUnm.getResultPart());
	}

	public void testCloseTreeTestResultRequest() throws Exception {
		TreeTestResult ttr = createCompleteTreeTestResult();
		ttr.close();

		CloseTreeTestResultRequest req = new CloseTreeTestResultRequest(ttr
				.getUuid(), ttr.getCloseDate());

		StringResult xml = marshallAndValidate(req);

		CloseTreeTestResultRequest reqUnm = (CloseTreeTestResultRequest) unmarshaller
				.unmarshal(new StringSource(xml.toString()));

		assertEquals(ttr.getUuid(), reqUnm.getResultUuid());
		assertDateSec(ttr.getCloseDate(), ttr.getCloseDate());
	}

	private StringResult marshallAndValidate(Object obj) throws Exception {
		StringResult xml = new StringResult();
		marshaller.marshal(obj, xml);

		log.info("Marshalled ResultPart Request: " + xml);

		UnitXmlUtils.assertXmlValidation(getBean(XmlValidator.class),
				new StringSource(xml.toString()));
		return xml;
	}
}
