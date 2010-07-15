/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

/** JMS based tree test result listener implementation. */
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

	/** Publishes the test result only when it gets closed. */
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
