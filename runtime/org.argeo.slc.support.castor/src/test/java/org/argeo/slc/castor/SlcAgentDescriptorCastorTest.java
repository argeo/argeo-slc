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

package org.argeo.slc.castor;

import java.util.ArrayList;
import java.util.List;

import org.argeo.slc.execution.ExecutionModuleDescriptor;
import org.argeo.slc.runtime.SlcAgentDescriptor;

public class SlcAgentDescriptorCastorTest extends AbstractCastorTestCase {
	public void testMarshUnmarshMini() throws Exception {
		SlcAgentDescriptor agentDescriptor = createMiniAgentDescriptor();
		SlcAgentDescriptor agentDescriptorUnm = marshUnmarsh(agentDescriptor,
				false);
		assertSlcAgentDescriptor(agentDescriptor, agentDescriptorUnm);
	}

	public void testMarshUnmarshWithModuleDescriptor() throws Exception {
		SlcAgentDescriptor agentDescriptor = createMiniAgentDescriptor();

		List<ExecutionModuleDescriptor> lst = new ArrayList<ExecutionModuleDescriptor>();
		ExecutionModuleDescriptor moduleDescriptor = new ExecutionModuleDescriptor();
		moduleDescriptor.setName("test.moodule");
		moduleDescriptor.setVersion("1.0.0");
		lst.add(moduleDescriptor);
		agentDescriptor.setModuleDescriptors(lst);

		SlcAgentDescriptor agentDescriptorUnm = marshUnmarsh(agentDescriptor,
				false);
		assertSlcAgentDescriptor(agentDescriptor, agentDescriptorUnm);
	}

	protected static SlcAgentDescriptor createMiniAgentDescriptor() {
		SlcAgentDescriptor agentDescriptor = new SlcAgentDescriptor();
		agentDescriptor.setHost("localhost");
		agentDescriptor.setUuid("555");
		return agentDescriptor;
	}

	protected static void assertSlcAgentDescriptor(SlcAgentDescriptor expected,
			SlcAgentDescriptor reached) {
		assertNotNull(reached);
		assertEquals(expected.getHost(), reached.getHost());
		assertEquals(expected.getUuid(), expected.getUuid());
		assertEquals(expected.getModuleDescriptors().size(), reached
				.getModuleDescriptors().size());
	}
}
