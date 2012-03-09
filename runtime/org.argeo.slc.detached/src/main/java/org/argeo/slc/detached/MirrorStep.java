/*
 * Copyright (C) 2007-2012 Mathieu Baudier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
