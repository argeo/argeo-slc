package org.argeo.slc.jms;

import javax.jms.Destination;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.msg.test.tree.CloseTreeTestResultRequest;
import org.argeo.slc.msg.test.tree.CreateTreeTestResultRequest;
import org.argeo.slc.msg.test.tree.ResultPartRequest;
import org.argeo.slc.test.TestResultListener;
import org.argeo.slc.test.TestResultPart;
import org.springframework.jms.core.JmsTemplate;

public class JmsTreeTestResultListener implements
		TestResultListener<TreeTestResult> {
	private final Log log = LogFactory.getLog(getClass());

	private Boolean onlyOnClose = false;
	private JmsTemplate jmsTemplate;

	private Destination executionEventDestination;
//	private Destination createDestination;
//	private Destination addResultPartDestination;
//	private Destination closeDestination;

	public void resultPartAdded(TreeTestResult testResult,
			TestResultPart testResultPart) {
		if (onlyOnClose)
			return;

		try {
			if (testResult.getResultParts().size() == 1
					&& testResult.getResultParts().values().iterator().next()
							.getParts().size() == 1) {
				CreateTreeTestResultRequest req = new CreateTreeTestResultRequest(
						testResult);

				if (log.isDebugEnabled())
					log.debug("Send create result request for result "
							+ testResult.getUuid());

				jmsTemplate.convertAndSend(executionEventDestination, req);
			} else {
				ResultPartRequest req = new ResultPartRequest(testResult);

				if (log.isDebugEnabled())
					log.debug("Send result parts for result "
							+ testResult.getUuid());

				jmsTemplate.convertAndSend(executionEventDestination, req);
			}
		} catch (Exception e) {
			throw new SlcException("Could not notify to JMS", e);
		}
	}

	public void close(TreeTestResult testResult) {
		try {
			if (onlyOnClose) {
				CreateTreeTestResultRequest req = new CreateTreeTestResultRequest(
						testResult);

				if (log.isDebugEnabled())
					log.debug("Send create result request for result "
							+ testResult.getUuid());

				jmsTemplate.convertAndSend(executionEventDestination, req);
			} else {
				CloseTreeTestResultRequest req = new CloseTreeTestResultRequest(
						testResult);

				if (log.isDebugEnabled())
					log.debug("Send close result request for result "
							+ testResult.getUuid());

				jmsTemplate.convertAndSend(executionEventDestination, req);

			}
		} catch (Exception e) {
			throw new SlcException("Could not notify to JMS", e);
		}
	}

	public void setOnlyOnClose(Boolean onlyOnClose) {
		this.onlyOnClose = onlyOnClose;
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	public void setExecutionEventDestination(Destination executionEventDestination) {
		this.executionEventDestination = executionEventDestination;
	}
	
	

//	public void setCreateDestination(Destination createDestination) {
//		this.createDestination = createDestination;
//	}
//
//	public void setAddResultPartDestination(Destination addResultPartDestination) {
//		this.addResultPartDestination = addResultPartDestination;
//	}
//
//	public void setCloseDestination(Destination closeDestination) {
//		this.closeDestination = closeDestination;
//	}

}
