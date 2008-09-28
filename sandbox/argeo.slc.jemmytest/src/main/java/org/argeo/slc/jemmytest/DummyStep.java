package org.argeo.slc.jemmytest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.detached.AutoUiActivator;
import org.argeo.slc.detached.DetachedContext;
import org.argeo.slc.detached.DetachedStep;
import org.argeo.slc.detached.DetachedStepAnswer;
import org.argeo.slc.detached.DetachedStepRequest;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JLabelOperator;

public class DummyStep implements DetachedStep {
	private final static Log log = LogFactory.getLog(DummyStep.class);

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
		log.info("textAfterPush=" + textAfterPush);

		return null;
	}

}
