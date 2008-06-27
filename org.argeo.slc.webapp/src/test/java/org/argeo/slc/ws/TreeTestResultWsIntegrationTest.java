package org.argeo.slc.ws;

import org.springframework.ws.client.core.WebServiceTemplate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static org.argeo.slc.unit.test.tree.TreeTestResultTestUtils.createCompleteTreeTestResult;
import static org.argeo.slc.unit.test.tree.TreeTestResultTestUtils.createSimpleResultPartRequest;

import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.msg.test.tree.CloseTreeTestResultRequest;
import org.argeo.slc.msg.test.tree.CreateTreeTestResultRequest;
import org.argeo.slc.msg.test.tree.ResultPartRequest;
import org.argeo.slc.unit.AbstractSpringTestCase;

public class TreeTestResultWsIntegrationTest extends AbstractSpringTestCase {
	private Log log = LogFactory.getLog(getClass());

	public void testCreateTreeTestResultRequest() {
		WebServiceTemplate template = getBean(WebServiceTemplate.class);
		CreateTreeTestResultRequest req = new CreateTreeTestResultRequest(
				createCompleteTreeTestResult());
		req.getTreeTestResult().close();// in order to avoid unclosed in test db

		log.info("Send CreateTreeTestResultRequest for result "
				+ req.getTreeTestResult().getUuid());

		template.marshalSendAndReceive(req);
	}

	public void testResultPartRequest() {
		WebServiceTemplate template = getBean(WebServiceTemplate.class);
		TreeTestResult ttr = createCompleteTreeTestResult();
		ttr.close();// in order to avoid unclosed in test db
		CreateTreeTestResultRequest reqCreate = new CreateTreeTestResultRequest(
				ttr);
		log.info("Send CreateTreeTestResultRequest for result "
				+ reqCreate.getTreeTestResult().getUuid());
		template.marshalSendAndReceive(reqCreate);

		ResultPartRequest req = createSimpleResultPartRequest(ttr);

		log.info("Send ResultPartRequest for result " + req.getResultUuid());
		template.marshalSendAndReceive(req);
	}

	public void testCloseTreeTestResultRequest() {
		WebServiceTemplate template = getBean(WebServiceTemplate.class);

		TreeTestResult ttr = createCompleteTreeTestResult();
		CreateTreeTestResultRequest reqCreate = new CreateTreeTestResultRequest(
				ttr);
		log.info("Send CreateTreeTestResultRequest for result "
				+ reqCreate.getTreeTestResult().getUuid());
		template.marshalSendAndReceive(reqCreate);

		ttr.close();
		CloseTreeTestResultRequest req = new CloseTreeTestResultRequest(ttr
				.getUuid(), ttr.getCloseDate());

		log.info("Send CloseTreeTestResultRequest for result "
				+ req.getResultUuid());

		template.marshalSendAndReceive(req);
	}
}
