package org.argeo.slc.client.ui.listeners;

import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.ClientUiPlugin;
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
import org.eclipse.ui.handlers.IHandlerService;

/** In memory access to a test manager service */
public class TestManagerServiceAdapter implements TreeTestResultListener {
	// private static final Log log = LogFactory
	// .getLog(TestManagerServiceAdapter.class);

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

		// TODO : clean this -> pb of thread && commandID hardCoded.
		// We force the refresh of the list view.
		ClientUiPlugin.getDefault().getWorkbench().getDisplay()
				.syncExec(new Runnable() {
					public void run() {
						IHandlerService handlerService = (IHandlerService) ClientUiPlugin
								.getDefault().getWorkbench()
								.getService(IHandlerService.class);
						try {
							handlerService
									.executeCommand(
											"org.argeo.slc.client.ui.refreshResultList",
											null);
							handlerService
									.executeCommand(
											"org.argeo.slc.client.ui.refreshProcessList",
											null);

						} catch (Exception e) {
							e.printStackTrace();
							throw new SlcException(
									"Problem while rendering result. "
											+ e.getMessage());
						}
					}
				}

				);

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
