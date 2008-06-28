package org.argeo.slc.ws;

import java.util.List;
import java.util.Vector;

import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.argeo.slc.core.process.SlcExecution;
import org.argeo.slc.core.process.SlcExecutionStep;
import org.argeo.slc.msg.process.SlcExecutionRequest;
import org.argeo.slc.msg.process.SlcExecutionStatusRequest;
import org.argeo.slc.msg.process.SlcExecutionStepsRequest;
import org.argeo.slc.unit.AbstractSpringTestCase;
import org.argeo.slc.unit.process.SlcExecutionTestUtils;
import org.argeo.slc.ws.client.WebServiceUtils;

public class SlcExecutionWsIntegrationTest extends AbstractSpringTestCase {
	private Log log = LogFactory.getLog(getClass());

	private WebServiceTemplate template;

	public void setUp() {
		template = getBean(WebServiceTemplate.class);
	}

	public void testSlcExecutionRequests() {
		SlcExecution slcExec = createAndSendSlcExecution();

		slcExec.setUser("otherUser");
		log.info("Send update SlcExecutionRequest for SlcExecution #"
				+ slcExec.getUuid());
		template.marshalSendAndReceive(new SlcExecutionRequest(slcExec));
	}

	public void testSlcExecutionStatusRequest() {
		SlcExecution slcExec = createAndSendSlcExecution();

		slcExec.setStatus(SlcExecution.STATUS_FINISHED);
		log.info("Send SlcExecutionStatusRequest for SlcExecution #"
				+ slcExec.getUuid());
		template.marshalSendAndReceive(new SlcExecutionStatusRequest(slcExec
				.getUuid(), slcExec.getStatus()));
	}

	public void testSendSlcExecutionStepRequest() {
		SlcExecution slcExec = createAndSendSlcExecution();

		SlcExecutionStep step1 = new SlcExecutionStep(
				"Logline\nAnother log line.");
		SlcExecutionStep step2 = new SlcExecutionStep(
				"Logline2\nAnother log line2.");
		List<SlcExecutionStep> steps = new Vector<SlcExecutionStep>();
		steps.add(step1);
		steps.add(step2);

		log.info("Send SlcExecutionStepsRequest for SlcExecution #"
				+ slcExec.getUuid());
		try {
			template.marshalSendAndReceive(new SlcExecutionStepsRequest(slcExec
					.getUuid(), steps));
		} catch (SoapFaultClientException e) {
			WebServiceUtils.manageSoapException(e);
			throw e;
		}
	}

	protected SlcExecution createAndSendSlcExecution() {
		SlcExecution slcExec = SlcExecutionTestUtils.createSimpleSlcExecution();

		log.info("Send create SlcExecutionRequest for SlcExecution #"
				+ slcExec.getUuid());
		template.marshalSendAndReceive(new SlcExecutionRequest(slcExec));
		return slcExec;
	}
}
