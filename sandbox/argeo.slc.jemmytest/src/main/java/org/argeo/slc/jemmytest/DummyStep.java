package org.argeo.slc.jemmytest;

import org.argeo.slc.autoui.AutoUiActivator;
import org.argeo.slc.autoui.DetachedContext;
import org.argeo.slc.autoui.DetachedStep;
import org.argeo.slc.autoui.DetachedStepAnswer;
import org.argeo.slc.autoui.DetachedStepRequest;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JLabelOperator;

public class DummyStep implements DetachedStep {
	public DetachedStepAnswer execute(DetachedContext detachedContext,
			DetachedStepRequest detachedStepRequest) {

		// Find components
		JFrameOperator jFrameOperator = new JFrameOperator("HelloWorldSwing");
		JButtonOperator jButtonOperator = new JButtonOperator(jFrameOperator,
				"Button");
		JLabelOperator jLabelOperator = new JLabelOperator(jFrameOperator,
				"Hello World");

		// Execute actions
		jButtonOperator.push();

		// Performs checks
		String textAfterPush = jLabelOperator.getText();
		AutoUiActivator.stdOut("textAfterPush=" + textAfterPush);

		return null;
	}

}
