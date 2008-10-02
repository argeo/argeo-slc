package org.argeo.slc.jemmytest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.detached.DetachedContext;
import org.argeo.slc.detached.DetachedStep;
import org.argeo.slc.detached.DetachedAnswer;
import org.argeo.slc.detached.DetachedRequest;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JLabelOperator;

public class DummyStep implements DetachedStep {
	private final static Log log = LogFactory.getLog(DummyStep.class);

	public DetachedAnswer execute(DetachedContext detachedContext,
			DetachedRequest request) {

		// Find components
		JFrameOperator jFrameOperator = new JFrameOperator("HelloWorldSwing");
		JButtonOperator jButtonOperator = new JButtonOperator(jFrameOperator,
				"Button");
		String label = request.getProperties().getProperty("jemmyTest.label");
		JLabelOperator jLabelOperator = new JLabelOperator(jFrameOperator,
				label);

		// Execute actions
		jButtonOperator.push();

		// Performs checks
		String textAfterPush = jLabelOperator.getText();
		log.info("textAfterPush=" + textAfterPush);

		DetachedAnswer answer = new DetachedAnswer(request,
				"DummyStep passed!! textAfterPush=" + textAfterPush);
		answer.getProperties().setProperty("jemmyTest.label", textAfterPush);
		return answer;
	}

}
