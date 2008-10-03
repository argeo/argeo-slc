package org.argeo.slc.jemmytest;

import org.argeo.slc.detached.DetachedContext;
import org.argeo.slc.detached.DetachedRequest;
import org.argeo.slc.detached.ui.UiPart;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JLabelOperator;

public class TestDialog implements UiPart {
	private JDialogOperator dialog = null;
	private JLabelOperator label = null;

	public void init(DetachedContext context, DetachedRequest request) {
		dialog = new JDialogOperator("TestDialog");
		label = new JLabelOperator(dialog, "Dialog Open");
	}

	public String getLabelText() {
		return label.getText();
	}

	public void close() {
		dialog.close();
	}
}
