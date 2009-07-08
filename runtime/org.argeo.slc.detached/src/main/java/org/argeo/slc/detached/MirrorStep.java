package org.argeo.slc.detached;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Dummy detached step which copies the request into the answer and log. Useful
 * for testing.
 */
public class MirrorStep implements DetachedStep {
	private final static Log log = LogFactory.getLog(MirrorStep.class);

	public DetachedAnswer execute(DetachedContext detachedContext,
			DetachedRequest req) {
		log.debug("  uuid=" + req.getUuid());
		log.debug("  ref=" + req.getRef());
		log.debug("  path=" + req.getPath());
		log.debug("  properties=" + req.getProperties());

		DetachedAnswer answer = new DetachedAnswer(req, "Mirror");
		Properties answerProps = new Properties();
		answerProps.putAll(req.getProperties());
		answer.setProperties(answerProps);
		return answer;
	}

}
