package org.argeo.slc.core.process;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.msg.process.SlcExecutionRequest;
import org.argeo.slc.unit.AbstractSpringTestCase;
import org.springframework.ws.client.core.WebServiceTemplate;

public class SlcExecutionWsIntegrationTest extends AbstractSpringTestCase {
	private Log log = LogFactory.getLog(getClass());

	public void testSendSlcExecutionrequest() {
		WebServiceTemplate template = getBean(WebServiceTemplate.class);
		SlcExecution slcExec = SlcExecutionTestUtils.createSimpleSlcExecution();

		SlcExecutionRequest req = new SlcExecutionRequest();
		req.setSlcExecution(slcExec);

		log.info("Send SlcExecutionRequest for SlcExecution "
				+ slcExec.getUuid());

		Object resp = template.marshalSendAndReceive(req);
		log.info("Resp: " + resp);
	}
}
