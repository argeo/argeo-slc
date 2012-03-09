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
package org.argeo.slc.server.unit;

import org.argeo.slc.msg.ExecutionAnswer;
import org.argeo.slc.server.client.SlcServerHttpClient;
import org.argeo.slc.unit.AbstractSpringTestCase;

public abstract class AbstractHttpClientTestCase extends AbstractSpringTestCase {
	private SlcServerHttpClient httpClient = null;

	protected void setUp() throws Exception {
		super.setUp();
		httpClient = createHttpClient();
		httpClient.waitForServerToBeReady();
	}

	protected SlcServerHttpClient createHttpClient() {
		SlcServerHttpClient httpClient = getBean(SlcServerHttpClient.class);
		return httpClient;
	}

	protected SlcServerHttpClient getHttpClient() {
		return httpClient;
	}

	protected void assertAnswerOk(ExecutionAnswer answer) {
		if (!answer.isOk()) {
			fail("Server execution answer is not ok: " + answer.getMessage());
		}
	}
}
