package org.argeo.slc.core.test.tree;

import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.xml.transform.StringResult;
import org.springframework.xml.transform.StringSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static org.argeo.slc.core.test.tree.TreeTestResultTestUtils.createCompleteTreeTestResult;

import org.argeo.slc.unit.AbstractSpringTestCase;
import org.argeo.slc.unit.test.tree.UnitTestTreeUtil;

public class TreeTestResultCastorTest extends AbstractSpringTestCase {
	private Log log = LogFactory.getLog(getClass());

	public void testMarshUnmarsh() throws Exception {
		Marshaller marshaller = getBean("marshaller");
		Unmarshaller unmarshaller = getBean("marshaller");

		TreeTestResult ttr = createCompleteTreeTestResult();

		StringResult xml = new StringResult();
		marshaller.marshal(ttr, xml);

		log.info("Marshalled TreeTestResult: " + xml);

		TreeTestResult ttrUnm = (TreeTestResult) unmarshaller
				.unmarshal(new StringSource(xml.toString()));

		UnitTestTreeUtil.assertTreeTestResult(ttr, ttrUnm);

	}
}
