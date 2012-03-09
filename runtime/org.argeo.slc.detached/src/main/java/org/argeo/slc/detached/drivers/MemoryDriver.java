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
package org.argeo.slc.detached.drivers;

import org.argeo.slc.detached.DetachedAnswer;
import org.argeo.slc.detached.DetachedClient;
import org.argeo.slc.detached.DetachedDriver;
import org.argeo.slc.detached.DetachedRequest;

/**
 * Implements both <code>DetachedClient</code> and <code>DetachedDriver</code>
 * using memory access
 */
public class MemoryDriver implements DetachedClient, DetachedDriver {
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

	public void stop() {
		// NOTHING
	}
}
