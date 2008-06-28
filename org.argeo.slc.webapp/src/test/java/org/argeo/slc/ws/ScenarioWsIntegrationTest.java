package org.argeo.slc.ws;

import static org.argeo.slc.unit.test.tree.TreeTestResultTestUtils.createMinimalConsistentTreeTestResult;
import static org.argeo.slc.unit.test.tree.TreeTestResultTestUtils.createSimpleResultPartError;
import static org.argeo.slc.unit.test.tree.TreeTestResultTestUtils.createSimpleResultPartFailed;
import static org.argeo.slc.unit.test.tree.TreeTestResultTestUtils.createSimpleResultPartPassed;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.core.process.SlcExecution;
import org.argeo.slc.core.process.SlcExecutionStep;
import org.argeo.slc.core.test.SimpleResultPart;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.msg.process.SlcExecutionRequest;
import org.argeo.slc.msg.process.SlcExecutionStatusRequest;
import org.argeo.slc.msg.process.SlcExecutionStepsRequest;
import org.argeo.slc.msg.test.tree.CloseTreeTestResultRequest;
import org.argeo.slc.msg.test.tree.CreateTreeTestResultRequest;
import org.argeo.slc.msg.test.tree.ResultPartRequest;
import org.argeo.slc.unit.AbstractSpringTestCase;
import org.argeo.slc.unit.process.SlcExecutionTestUtils;
import org.springframework.ws.client.core.WebServiceTemplate;

public class ScenarioWsIntegrationTest extends AbstractSpringTestCase {
	private Log log = LogFactory.getLog(getClass());

	private WebServiceTemplate template;

	public void setUp() {
		template = getBean(WebServiceTemplate.class);
	}

	public void testSinglePathScenario() {
		// Create SLC execution
		SlcExecution slcExec = SlcExecutionTestUtils.createSimpleSlcExecution();
		log.info("Send create SlcExecutionRequest for SlcExecution #"
				+ slcExec.getUuid());
		template.marshalSendAndReceive(new SlcExecutionRequest(slcExec));

		// Add SLC execution step
		SlcExecutionStep step = new SlcExecutionStep("JUnit step");
		slcExec.getSteps().add(step);
		log.info("Send SlcExecutionStepsRequest for SlcExecution #"
				+ slcExec.getUuid());
		template.marshalSendAndReceive(new SlcExecutionStepsRequest(slcExec
				.getUuid(), step));

		// Create test result
		TreeTestResult ttr = createMinimalConsistentTreeTestResult(slcExec);
		ttr.addResultPart(createSimpleResultPartPassed());
		log.info("Send CreateTreeTestResultRequest for result #"
				+ ttr.getUuid());
		template.marshalSendAndReceive(new CreateTreeTestResultRequest(ttr));

		// Add failed part
		SimpleResultPart failedPart = createSimpleResultPartFailed();
		ttr.addResultPart(failedPart);
		log.info("Send ResultPartRequest for result #" + ttr.getUuid());
		template.marshalSendAndReceive(new ResultPartRequest(ttr, null,
				failedPart));

		// Add error part
		SimpleResultPart errorPart = createSimpleResultPartError();
		ttr.addResultPart(errorPart);
		log.info("Send ResultPartRequest for result #" + ttr.getUuid());
		template.marshalSendAndReceive(new ResultPartRequest(ttr, null,
				errorPart));

		// Close result
		ttr.close();
		log
				.info("Send CloseTreeTestResultRequest for result #"
						+ ttr.getUuid());
		template.marshalSendAndReceive(new CloseTreeTestResultRequest(ttr
				.getUuid(), ttr.getCloseDate()));

		// Notify SLC execution FINISHED
		slcExec.setStatus(SlcExecution.STATUS_FINISHED);
		log.info("Send SlcExecutionStatusRequest for SlcExecution #"
				+ slcExec.getUuid());
		template.marshalSendAndReceive(new SlcExecutionStatusRequest(slcExec
				.getUuid(), slcExec.getStatus()));
	}

}
