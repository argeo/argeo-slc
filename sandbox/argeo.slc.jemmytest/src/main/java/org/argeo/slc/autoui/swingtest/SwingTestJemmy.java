package org.argeo.slc.autoui.swingtest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.jemmy.ClassReference;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JFrameOperator;

public class SwingTestJemmy {
	private final static Log log = LogFactory.getLog(SwingTestJemmy.class);

	public static void main(String[] args) {
		try {
			ClassReference classReference = new ClassReference(
					SwingTestUi.class.getName());
			classReference.startApplication();
			JFrameOperator jFrameOperator = new JFrameOperator(
					"HelloWorldSwing");
			JButtonOperator jButtonOperator = new JButtonOperator(
					jFrameOperator, "Button");
			jButtonOperator.push();
			String textAfterPush = jButtonOperator.getText();
			log.info("textAfterPush=" + textAfterPush);
			
			jFrameOperator.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
