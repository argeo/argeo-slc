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

import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.argeo.slc.execution.ExecutionModuleDescriptor;
import org.argeo.slc.msg.ObjectList;
import org.argeo.slc.runtime.SlcAgent;
import org.argeo.slc.runtime.SlcAgentFactory;
import org.argeo.slc.web.mvc.AbstractServiceController;
import org.springframework.web.servlet.ModelAndView;

/** . */
public class ListModulesDescriptors extends AbstractServiceController {
	private SlcAgentFactory agentFactory;

	@Override
	protected void handleServiceRequest(HttpServletRequest request,
			HttpServletResponse response, ModelAndView modelAndView)
			throws Exception {

		// TODO: use centralized agentId property (from MsgConstants)?
		String agentId = request.getParameter("agentId");

		SlcAgent slcAgent = agentFactory.getAgent(agentId);

		List<ExecutionModuleDescriptor> descriptors = slcAgent
				.listExecutionModuleDescriptors();
		SortedSet<ExecutionModuleDescriptor> set = new TreeSet<ExecutionModuleDescriptor>(
				new Comparator<ExecutionModuleDescriptor>() {

					public int compare(ExecutionModuleDescriptor md1,
							ExecutionModuleDescriptor md2) {
						String str1 = md1.getLabel() != null ? md1.getLabel()
								: md1.getName();
						String str2 = md2.getLabel() != null ? md2.getLabel()
								: md2.getName();
						return str1.compareTo(str2);
					}
				});
		set.addAll(descriptors);

		modelAndView.addObject(new ObjectList(set));
	}

	public void setAgentFactory(SlcAgentFactory agentFactory) {
		this.agentFactory = agentFactory;
	}

}
