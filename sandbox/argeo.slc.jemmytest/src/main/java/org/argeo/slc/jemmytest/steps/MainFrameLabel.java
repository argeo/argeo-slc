package org.argeo.slc.jemmytest.steps;

import org.argeo.slc.detached.DetachedAnswer;
import org.argeo.slc.detached.DetachedContext;
import org.argeo.slc.detached.DetachedRequest;
import org.argeo.slc.detached.ui.UiStep;
import org.argeo.slc.jemmytest.uiparts.MainFrame;

public class MainFrameLabel extends UiStep {

	protected DetachedAnswer executeUiStep(DetachedContext context,
			DetachedRequest request) {
		MainFrame mainFrame = (MainFrame) getUiPart();
		mainFrame.changeLabel();
		String textAfterPush = mainFrame.getLabelText();

		DetachedAnswer answer = new DetachedAnswer(request,
				"DummyStep passed!! textAfterPush=" + textAfterPush);
		answer.getProperties().setProperty("jemmyTest.label", textAfterPush);
		return answer;
	}

}
