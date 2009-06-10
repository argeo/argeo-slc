package org.argeo.slc.jms;

import javax.jms.Destination;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.core.attachment.Attachment;
import org.argeo.slc.core.attachment.SimpleAttachment;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.core.test.tree.TreeTestResultListener;
import org.argeo.slc.msg.test.tree.AddTreeTestResultAttachmentRequest;
import org.argeo.slc.msg.test.tree.CloseTreeTestResultRequest;
import org.argeo.slc.msg.test.tree.CreateTreeTestResultRequest;
import org.argeo.slc.msg.test.tree.ResultPartRequest;
import org.argeo.slc.test.TestResultPart;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;

public class JmsTreeTestResultListener implements TreeTestResultListener {
	private final Log log = LogFactory.getLog(getClass());

	private Boolean onlyOnClose = false;
	private JmsTemplate jmsTemplate;

	private Destination executionEventDestination;

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
		} catch (JmsException e) {
			log.warn("Could not notify result part to server: "
					+ e.getMessage());
			if (log.isTraceEnabled())
				log.debug("Original error.", e);
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
		} catch (JmsException e) {
			log.warn("Could not notify result close to server: "
					+ e.getMessage());
			if (log.isTraceEnabled())
				log.debug("Original error.", e);
		} catch (Exception e) {
			throw new SlcException("Could not notify to JMS", e);
		}
	}

	public void addAttachment(TreeTestResult testResult, Attachment attachment) {
		try {
			AddTreeTestResultAttachmentRequest req = new AddTreeTestResultAttachmentRequest();
			req.setResultUuid(testResult.getUuid());
			req.setAttachment((SimpleAttachment) attachment);
			jmsTemplate.convertAndSend(executionEventDestination, req);

		} catch (JmsException e) {
			log
					.warn("Could not notify attachment to server: "
							+ e.getMessage());
			if (log.isTraceEnabled())
				log.debug("Original error.", e);
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

	public void setExecutionEventDestination(
			Destination executionEventDestination) {
		this.executionEventDestination = executionEventDestination;
	}

}
