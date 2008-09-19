package org.argeo.slc.autoui.internal;

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
			throw new RuntimeException("Could not execute as Runnable", e);
		}
	}

	public Object execute(Object object) throws Exception {

		String className = "org.argeo.slc.jemmytest.ui.SwingTestUi";
		// String[] args = {};
		// SwingTestUi.main(args);
		// Start application
		ClassReference classReference = new ClassReference(className);
		String[] args = { "noExitOnClose" };
		classReference.startApplication(args);

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
