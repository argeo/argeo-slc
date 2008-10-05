package org.argeo.slc.jemmytest.steps;

import org.argeo.slc.detached.DetachedAnswer;
import org.argeo.slc.detached.DetachedContext;
import org.argeo.slc.detached.DetachedRequest;
import org.argeo.slc.detached.ui.UiStep;
import org.argeo.slc.jemmytest.uiparts.TestDialog;

public class TestDialogLabel extends UiStep {
	private TestDialog testDialog;

	protected DetachedAnswer executeUiStep(DetachedContext context,
			DetachedRequest request) {
		String labelText = testDialog.getLabelText();
		testDialog.close();
		
		DetachedAnswer answer = new DetachedAnswer(request,
				"TestDialogLabel executed");
		answer.getProperties().setProperty("jemmyTest.labelDialog", labelText);
		return answer;
	}

	public void setTestDialog(TestDialog testDialog) {
		this.testDialog = testDialog;
	}

}
