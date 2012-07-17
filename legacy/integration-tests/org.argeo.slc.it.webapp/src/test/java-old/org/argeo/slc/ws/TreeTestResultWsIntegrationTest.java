/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
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

package org.argeo.slc.ws;

import org.springframework.ws.client.core.WebServiceTemplate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static org.argeo.slc.unit.test.tree.TreeTestResultTestUtils.createCompleteTreeTestResult;
import static org.argeo.slc.unit.test.tree.TreeTestResultTestUtils.createSimpleResultPartRequest;

import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.msg.test.tree.CloseTreeTestResultRequest;
import org.argeo.slc.msg.test.tree.CreateTreeTestResultRequest;
import org.argeo.slc.msg.test.tree.ResultPartRequest;
import org.argeo.slc.unit.AbstractSpringTestCase;

public class TreeTestResultWsIntegrationTest extends AbstractSpringTestCase {
	private Log log = LogFactory.getLog(getClass());

	private WebServiceTemplate template;

	public void setUp() {
		template = getBean(WebServiceTemplate.class);
	}

	public void testCreateTreeTestResultRequest() {
		createAndSendTreeTestResult(true);
	}

	public void testResultPartRequest() {
		TreeTestResult ttr = createAndSendTreeTestResult(true);

		ResultPartRequest req = createSimpleResultPartRequest(ttr);

		log.info("Send ResultPartRequest for result " + req.getResultUuid());
		template.marshalSendAndReceive(req);
	}

	public void testCloseTreeTestResultRequest() {
		TreeTestResult ttr = createAndSendTreeTestResult(false);

		ttr.close();
		CloseTreeTestResultRequest req = new CloseTreeTestResultRequest(ttr
				.getUuid(), ttr.getCloseDate());
		log.info("Send CloseTreeTestResultRequest for result "
				+ req.getResultUuid());
		template.marshalSendAndReceive(req);
	}

	protected TreeTestResult createAndSendTreeTestResult(boolean close) {
		TreeTestResult ttr = createCompleteTreeTestResult();
		log.info("Send CreateTreeTestResultRequest for result #"
				+ ttr.getUuid());
		template.marshalSendAndReceive(new CreateTreeTestResultRequest(ttr));

		if (close)
			ttr.close();
		return ttr;
	}
}
