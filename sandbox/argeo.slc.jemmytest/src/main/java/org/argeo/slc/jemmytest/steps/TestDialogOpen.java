package org.argeo.slc.jemmytest.steps;

import org.argeo.slc.detached.DetachedAnswer;
import org.argeo.slc.detached.DetachedContext;
import org.argeo.slc.detached.DetachedRequest;
import org.argeo.slc.detached.ui.UiStep;
import org.argeo.slc.jemmytest.uiparts.MainFrame;
import org.argeo.slc.jemmytest.uiparts.TestDialog;

public class TestDialogOpen extends UiStep {
	private MainFrame mainFrame;
	private TestDialog testDialog;

	protected DetachedAnswer executeUiStep(DetachedContext context,
			DetachedRequest request) {

		mainFrame.openDialog();
		testDialog.init(context, request);

		DetachedAnswer answer = new DetachedAnswer(request,
				"TestDialogOpen executed");
		return answer;
	}

	public void setTestDialog(TestDialog testDialog) {
		this.testDialog = testDialog;
	}

	public void setMainFrame(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

}
