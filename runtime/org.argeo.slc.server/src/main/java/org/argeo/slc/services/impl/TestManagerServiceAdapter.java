package org.argeo.slc.services.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.core.attachment.Attachment;
import org.argeo.slc.core.attachment.SimpleAttachment;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.core.test.tree.TreeTestResultListener;
import org.argeo.slc.msg.test.tree.AddTreeTestResultAttachmentRequest;
import org.argeo.slc.msg.test.tree.CloseTreeTestResultRequest;
import org.argeo.slc.msg.test.tree.CreateTreeTestResultRequest;
import org.argeo.slc.msg.test.tree.ResultPartRequest;
import org.argeo.slc.services.TestManagerService;
import org.argeo.slc.test.TestResultPart;

public class TestManagerServiceAdapter implements TreeTestResultListener {

	private final Log log = LogFactory.getLog(getClass());

	private Boolean onlyOnClose = false;

	private TestManagerService testManagerService;

	public void resultPartAdded(TreeTestResult testResult,
			TestResultPart testResultPart) {
		if (onlyOnClose)
			return;

		if (testResult.getResultParts().size() == 1
				&& testResult.getResultParts().values().iterator().next()
						.getParts().size() == 1) {
			CreateTreeTestResultRequest req = new CreateTreeTestResultRequest(
					testResult);

			if (log.isTraceEnabled())
				log.trace("Send create result request for result "
						+ testResult.getUuid());

			testManagerService.createTreeTestResult(req);
		} else {
			ResultPartRequest req = new ResultPartRequest(testResult);

			if (log.isTraceEnabled())
				log.trace("Send result parts for result "
						+ testResult.getUuid());

			testManagerService.addResultPart(req);
		}
	}

	public void close(TreeTestResult testResult) {
		if (onlyOnClose) {
			CreateTreeTestResultRequest req = new CreateTreeTestResultRequest(
					testResult);

			if (log.isTraceEnabled())
				log.trace("Send onClose create result request for result "
						+ testResult.getUuid());

			testManagerService.createTreeTestResult(req);
		} else {
			CloseTreeTestResultRequest req = new CloseTreeTestResultRequest(
					testResult);

			if (log.isTraceEnabled())
				log.trace("Send close result request for result "
						+ testResult.getUuid());

			testManagerService.closeTreeTestResult(req);

		}
	}

	public void addAttachment(TreeTestResult testResult, Attachment attachment) {
		if (onlyOnClose)
			return;

		AddTreeTestResultAttachmentRequest req = new AddTreeTestResultAttachmentRequest();
		req.setResultUuid(testResult.getUuid());
		req.setAttachment((SimpleAttachment) attachment);
		testManagerService.addAttachment(req);

	}

	/** Publishes the test result only when it gets closed. */
	public void setOnlyOnClose(Boolean onlyOnClose) {
		this.onlyOnClose = onlyOnClose;
	}

	public void setTestManagerService(TestManagerService testManagerService) {
		this.testManagerService = testManagerService;
	}

}
