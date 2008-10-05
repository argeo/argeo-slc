package org.argeo.slc.jemmytest.steps;

import org.argeo.slc.detached.DetachedAnswer;
import org.argeo.slc.detached.DetachedContext;
import org.argeo.slc.detached.DetachedRequest;
import org.argeo.slc.detached.ui.UiStep;
import org.argeo.slc.jemmytest.uiparts.ComplexPanel;

public class ComplexPanelValues extends UiStep {
	private ComplexPanel complexPanel;

	protected DetachedAnswer executeUiStep(DetachedContext context,
			DetachedRequest request) {
		complexPanel.computeTime();
		String time = complexPanel.getTime();

		DetachedAnswer answer = new DetachedAnswer(request,
				"ComplexPanelValues executed");
		answer.getProperties().setProperty("jemmyTest.complexPanel.time", time);
		return answer;
	}

	public void setComplexPanel(ComplexPanel complexPanel) {
		this.complexPanel = complexPanel;
	}

}
