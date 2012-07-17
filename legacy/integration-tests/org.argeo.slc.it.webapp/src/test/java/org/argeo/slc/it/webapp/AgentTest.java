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

package org.argeo.slc.it.webapp;

import java.net.InetAddress;

import org.argeo.slc.runtime.SlcAgentDescriptor;
import org.argeo.slc.server.unit.AbstractHttpClientTestCase;

public class AgentTest extends AbstractHttpClientTestCase {
	public void testListAgents() throws Exception {
		SlcAgentDescriptor slcAgentDescriptor = getHttpClient()
				.waitForOneAgent();
		assertEquals(InetAddress.getLocalHost().getHostName(),
				slcAgentDescriptor.getHost());
	}

}
