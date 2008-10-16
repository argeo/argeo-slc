package org.argeo.slc.jemmytest.uiparts;

import java.awt.Container;

import org.argeo.slc.detached.DetachedContext;
import org.argeo.slc.detached.DetachedRequest;
import org.argeo.slc.detached.ui.UiPart;
import org.netbeans.jemmy.operators.ContainerOperator;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JLabelOperator;

public class MainFrame extends UiPart {
	private JFrameOperator frame = null;
	private JButtonOperator button = null;
	private JButtonOperator buttonStart = null;
	private JLabelOperator label = null;

	private ComplexPanel complexPanel;
	public final static String PROP_CONTENT_PANE = "jemmytest.ui.contentPane";

	protected void initUi(DetachedContext context, DetachedRequest request) {
		frame = new JFrameOperator("Mx");
		button = new JButtonOperator(frame, "Button");
		buttonStart = new JButtonOperator(frame, "Start");
		String labelStr = request.getProperties()
				.getProperty("jemmyTest.label");
		label = new JLabelOperator(frame, labelStr);

		context.setDynamicRef(MainFrame.PROP_CONTENT_PANE, frame
				.getContentPane());
		complexPanel.init(context, request);
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

	public void setComplexPanel(ComplexPanel complexPanel) {
		this.complexPanel = complexPanel;
	}

}
