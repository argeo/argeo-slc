package org.argeo.slc.core.test.tree;

import org.springframework.ws.client.core.WebServiceTemplate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static org.argeo.slc.core.test.tree.TreeTestResultTestUtils.createCompleteTreeTestResult;

import org.argeo.slc.msg.test.tree.CreateTreeTestResultRequest;
import org.argeo.slc.unit.AbstractSpringTestCase;

public class TreeTestResultWsIntegrationTest extends AbstractSpringTestCase {
	private Log log = LogFactory.getLog(getClass());

	public void testCreateTreeTestResultRequest() {
		WebServiceTemplate template = getBean(WebServiceTemplate.class);
		CreateTreeTestResultRequest req = new CreateTreeTestResultRequest();
		req.setTreeTestResult(createCompleteTreeTestResult());

		log.info("Send SlcExecutionRequest for SlcExecution "
				+ req.getTreeTestResult().getUuid());

		Object resp = template.marshalSendAndReceive(req);
		log.info("Resp: " + resp);
	}
}
