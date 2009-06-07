package org.argeo.slc.server.unit;

import org.argeo.slc.SlcException;
import org.argeo.slc.msg.ExecutionAnswer;
import org.argeo.slc.server.client.SlcServerHttpClient;
import org.argeo.slc.unit.AbstractSpringTestCase;

public abstract class AbstractHttpClientTestCase extends AbstractSpringTestCase {
	private SlcServerHttpClient httpClient = null;

	private String isServerReadyService = "isServerReady.service";

	protected void setUp() throws Exception {
		super.setUp();
		httpClient = createHttpClient();
		waitForServerToBeReady();
	}

	protected void waitForServerToBeReady() {
		ExecutionAnswer answer = httpClient.callServiceSafe(
				isServerReadyService, null, null, getServerReadyTimeout());
		if (!answer.isOk())
			throw new SlcException("Server is not ready: " + answer);
	}

	protected SlcServerHttpClient createHttpClient() {
		SlcServerHttpClient httpClient = getBean(SlcServerHttpClient.class);
		return httpClient;
	}

	protected SlcServerHttpClient getHttpClient() {
		return httpClient;
	}

	/** Default is 120s */
	protected Long getServerReadyTimeout() {
		return 120 * 1000l;
	}
}
