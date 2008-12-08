package org.argeo.slc.castor;

import static org.argeo.slc.unit.test.tree.TreeTestResultTestUtils.createCompleteTreeTestResult;

import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.core.test.tree.TreeTestResultCollection;
import org.argeo.slc.unit.test.tree.UnitTestTreeUtil;
import org.springframework.xml.transform.StringResult;

public class TreeTestResultCollectionCastorTest extends AbstractCastorTestCase {
	public void testMarshUnmarsh() throws Exception {
		TreeTestResult ttr = createCompleteTreeTestResult();
		TreeTestResult ttr2 = createCompleteTreeTestResult();

		TreeTestResultCollection ttrc = new TreeTestResultCollection();
		ttrc.setId("testCollection");
		ttrc.getResults().add(ttr);
		ttrc.getResults().add(ttr2);

		StringResult xml = marshalAndValidate(ttrc);

		TreeTestResultCollection ttrcUnm = unmarshal(xml);

		assertEquals(ttrc.getId(), ttrcUnm.getId());
		assertEquals(ttrc.getResults().size(), ttrcUnm.getResults().size());
		for (TreeTestResult ttrT : ttrc.getResults()) {
			if (ttrT.getUuid().equals(ttr.getUuid()))
				UnitTestTreeUtil.assertTreeTestResult(ttr, ttrT);
			else
				UnitTestTreeUtil.assertTreeTestResult(ttr2, ttrT);
		}
	}
}
