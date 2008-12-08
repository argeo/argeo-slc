package org.argeo.slc.castor;

import static org.argeo.slc.unit.UnitUtils.assertDateSec;
import static org.argeo.slc.unit.test.tree.TreeTestResultTestUtils.createCompleteTreeTestResult;
import static org.argeo.slc.unit.test.tree.TreeTestResultTestUtils.createSimpleResultPartRequest;

import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.msg.test.tree.CloseTreeTestResultRequest;
import org.argeo.slc.msg.test.tree.CreateTreeTestResultRequest;
import org.argeo.slc.msg.test.tree.ResultPartRequest;
import org.argeo.slc.unit.test.tree.UnitTestTreeUtil;
import org.springframework.xml.transform.StringResult;

public class TreeTestResultCastorTest extends AbstractCastorTestCase {
	public void testMarshUnmarsh() throws Exception {
		TreeTestResult ttr = createCompleteTreeTestResult();

		StringResult xml = marshalAndValidate(ttr);

		TreeTestResult ttrUnm = unmarshal(xml);

		UnitTestTreeUtil.assertTreeTestResult(ttr, ttrUnm);
	}

	public void testCreateTreeTestResultRequest() throws Exception {
		CreateTreeTestResultRequest req = new CreateTreeTestResultRequest();
		req.setTreeTestResult(createCompleteTreeTestResult());

		StringResult xml = marshalAndValidate(req);

		CreateTreeTestResultRequest reqUnm = unmarshal(xml);

		UnitTestTreeUtil.assertTreeTestResult(req.getTreeTestResult(), reqUnm
				.getTreeTestResult());
	}

	public void testResultPartRequest() throws Exception {
		TreeTestResult ttr = createCompleteTreeTestResult();
		ResultPartRequest req = createSimpleResultPartRequest(ttr);

		StringResult xml = marshalAndValidate(req);

		ResultPartRequest reqUnm = unmarshal(xml);

		UnitTestTreeUtil
				.assertPart(req.getResultPart(), reqUnm.getResultPart());
	}

	public void testCloseTreeTestResultRequest() throws Exception {
		TreeTestResult ttr = createCompleteTreeTestResult();
		ttr.close();

		CloseTreeTestResultRequest req = new CloseTreeTestResultRequest(ttr
				.getUuid(), ttr.getCloseDate());

		StringResult xml = marshalAndValidate(req);

		CloseTreeTestResultRequest reqUnm = unmarshal(xml);

		assertEquals(ttr.getUuid(), reqUnm.getResultUuid());
		assertDateSec(ttr.getCloseDate(), ttr.getCloseDate());
	}
}
