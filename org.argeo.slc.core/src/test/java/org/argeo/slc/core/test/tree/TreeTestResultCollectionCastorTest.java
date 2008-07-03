package org.argeo.slc.core.test.tree;

import static org.argeo.slc.unit.UnitUtils.assertDateSec;
import static org.argeo.slc.unit.test.tree.TreeTestResultTestUtils.createCompleteTreeTestResult;
import static org.argeo.slc.unit.test.tree.TreeTestResultTestUtils.createSimpleResultPartRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.msg.test.tree.CloseTreeTestResultRequest;
import org.argeo.slc.msg.test.tree.CreateTreeTestResultRequest;
import org.argeo.slc.msg.test.tree.ResultPartRequest;
import org.argeo.slc.unit.AbstractSpringTestCase;
import org.argeo.slc.unit.UnitXmlUtils;
import org.argeo.slc.unit.test.tree.UnitTestTreeUtil;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.xml.transform.StringResult;
import org.springframework.xml.transform.StringSource;
import org.springframework.xml.validation.XmlValidator;

public class TreeTestResultCollectionCastorTest extends AbstractSpringTestCase {
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
		TreeTestResult ttr2 = createCompleteTreeTestResult();

		TreeTestResultCollection ttrc = new TreeTestResultCollection();
		ttrc.setId("testCollection");
		ttrc.getResults().add(ttr);
		ttrc.getResults().add(ttr2);

		StringResult xml = marshallAndValidate(ttrc);

		TreeTestResultCollection ttrcUnm = (TreeTestResultCollection) unmarshaller
				.unmarshal(new StringSource(xml.toString()));

		assertEquals(ttrc.getId(), ttrcUnm.getId());
		assertEquals(ttrc.getResults().size(), ttrcUnm.getResults().size());
		for (TreeTestResult ttrT : ttrc.getResults()) {
			if (ttrT.getUuid().equals(ttr.getUuid()))
				UnitTestTreeUtil.assertTreeTestResult(ttr, ttrT);
			else
				UnitTestTreeUtil.assertTreeTestResult(ttr2, ttrT);
		}
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
