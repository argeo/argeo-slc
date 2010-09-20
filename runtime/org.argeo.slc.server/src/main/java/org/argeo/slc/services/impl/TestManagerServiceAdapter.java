package org.argeo.slc.services.impl;

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

/** In memory access to a test manager service */
public class TestManagerServiceAdapter implements TreeTestResultListener {
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
			testManagerService.createTreeTestResult(req);
		} else {
			ResultPartRequest req = new ResultPartRequest(testResult);
			testManagerService.addResultPart(req);
		}
	}

	public void close(TreeTestResult testResult) {
		if (onlyOnClose) {
			CreateTreeTestResultRequest req = new CreateTreeTestResultRequest(
					testResult);
			testManagerService.createTreeTestResult(req);
		} else {
			CloseTreeTestResultRequest req = new CloseTreeTestResultRequest(
					testResult);
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
