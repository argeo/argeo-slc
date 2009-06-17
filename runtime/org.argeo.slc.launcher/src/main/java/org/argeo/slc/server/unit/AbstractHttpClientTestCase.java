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
