package org.argeo.slc.jemmytest;

import org.argeo.slc.detached.DetachedContext;
import org.argeo.slc.detached.DetachedRequest;
import org.argeo.slc.detached.ui.UiPart;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JLabelOperator;

public class MainFrame implements UiPart {
	private JFrameOperator frame = null;
	private JButtonOperator button = null;
	private JButtonOperator buttonStart = null;
	private JLabelOperator label = null;

	public void init(DetachedContext context, DetachedRequest request) {
		frame = new JFrameOperator("HelloWorldSwing");
		button = new JButtonOperator(frame, "Button");
		buttonStart = new JButtonOperator(frame, "Start");
		String labelStr = request.getProperties()
				.getProperty("jemmyTest.label");
		label = new JLabelOperator(frame, labelStr);
	}

	public void changeLabel() {
		button.push();
	}

	public void openDialog() {
		buttonStart.push();
	}

	public String getLabelText() {
		return label.getText();
	}
}
