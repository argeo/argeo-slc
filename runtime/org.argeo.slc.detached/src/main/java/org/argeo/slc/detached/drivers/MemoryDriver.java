package org.argeo.slc.detached.drivers;

import org.argeo.slc.detached.DetachedAnswer;
import org.argeo.slc.detached.DetachedClient;
import org.argeo.slc.detached.DetachedRequest;

public class MemoryDriver extends AbstractDriver implements DetachedClient {
	private DetachedRequest currentRequest = null;
	private DetachedAnswer currentAnswer = null;

	// DRIVER
	public synchronized DetachedRequest receiveRequest() throws Exception {
		while (currentRequest == null)
			this.wait(500);
		return currentRequest;
	}

	public synchronized void sendAnswer(DetachedAnswer answer) throws Exception {
		currentAnswer = answer;
		this.notify();
	}

	// CLIENT
	public synchronized DetachedAnswer receiveAnswer() throws Exception {
		while (currentAnswer == null)
			this.wait(500);
		DetachedAnswer answer = currentAnswer;
		currentAnswer = null;
		currentRequest = null;
		return answer;
	}

	public synchronized void sendRequest(DetachedRequest request)
			throws Exception {
		currentRequest = request;
		this.notify();
	}

}
