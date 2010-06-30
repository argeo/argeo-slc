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

package org.argeo.slc.web.mvc.execution;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.argeo.slc.execution.ExecutionModuleDescriptor;
import org.argeo.slc.runtime.SlcAgent;
import org.argeo.slc.runtime.SlcAgentFactory;
import org.argeo.slc.web.mvc.AbstractServiceController;
import org.springframework.web.servlet.ModelAndView;

/** . */
public class GetModuleDescriptor extends AbstractServiceController {
	private SlcAgentFactory agentFactory;

	@Override
	protected void handleServiceRequest(HttpServletRequest request,
			HttpServletResponse response, ModelAndView modelAndView)
			throws Exception {

		String agentId = request.getParameter("agentId");
		String moduleName = request.getParameter("moduleName");
		String version = request.getParameter("version");

		SlcAgent slcAgent = agentFactory.getAgent(agentId);

		ExecutionModuleDescriptor md = slcAgent.getExecutionModuleDescriptor(
				moduleName, version);
		modelAndView.addObject(md);
	}

	public void setAgentFactory(SlcAgentFactory agentFactory) {
		this.agentFactory = agentFactory;
	}

}
