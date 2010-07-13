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

package org.argeo.slc.web.mvc.controllers;

import java.util.List;
import java.util.Vector;

import org.argeo.slc.dao.runtime.SlcAgentDescriptorDao;
import org.argeo.slc.msg.ExecutionAnswer;
import org.argeo.slc.msg.ObjectList;
import org.argeo.slc.runtime.SlcAgentDescriptor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controle and information about the agents.
 */

@Controller
public class AgentController {

	// IoC
	private SlcAgentDescriptorDao slcAgentDescriptorDao;

	@RequestMapping("/listAgents.service")
	protected ObjectList listAgents() {
		List<SlcAgentDescriptor> list = slcAgentDescriptorDao
				.listSlcAgentDescriptors();
		return new ObjectList(list);
	}

	@RequestMapping("/cleanAgents.service")
	protected ExecutionAnswer cleanAgents() {

		List<SlcAgentDescriptor> list = slcAgentDescriptorDao
				.listSlcAgentDescriptors();
		for (SlcAgentDescriptor t : new Vector<SlcAgentDescriptor>(list)) {
			slcAgentDescriptorDao.delete(t);
		}
		return ExecutionAnswer.ok("Execution completed properly");
	}

	// IoC
	public void setSlcAgentDescriptorDao(
			SlcAgentDescriptorDao slcAgentDescriptorDao) {
		this.slcAgentDescriptorDao = slcAgentDescriptorDao;
	}

}
