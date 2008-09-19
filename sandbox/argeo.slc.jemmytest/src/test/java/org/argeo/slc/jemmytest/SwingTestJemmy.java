package org.argeo.slc.jemmytest;

import junit.framework.TestCase;

import org.argeo.slc.jemmytest.ui.SwingTestUi;
import org.netbeans.jemmy.ClassReference;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JLabelOperator;

public class SwingTestJemmy extends TestCase {
//	private final static Log log = LogFactory.getLog(SwingTestJemmy.class);

	public void testSimple() throws Exception {
		// Start application
		ClassReference classReference = new ClassReference(SwingTestUi.class
				.getName());
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
		assertEquals("Pressed!!", textAfterPush);

		// Clean up
		jFrameOperator.close();
	}

}
