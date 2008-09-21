package org.argeo.slc.jemmytest;

import org.argeo.slc.autoui.AutoUiActivator;
import org.argeo.slc.autoui.AutoUiApplication;
import org.netbeans.jemmy.ClassReference;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JLabelOperator;

public class AutoUiApplicationJemmy implements AutoUiApplication {
	public void run() {
		try {
			execute(new Object());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Could not execute as Runnable"
					+ e.getMessage());
		}
	}

	public Object execute(Object object) throws Exception {

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
