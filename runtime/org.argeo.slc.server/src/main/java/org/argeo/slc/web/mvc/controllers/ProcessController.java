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

import java.io.BufferedReader;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.core.attachment.AttachmentsStorage;
import org.argeo.slc.dao.process.SlcExecutionDao;
import org.argeo.slc.execution.ExecutionModuleDescriptor;
import org.argeo.slc.msg.ExecutionAnswer;
import org.argeo.slc.msg.MsgConstants;
import org.argeo.slc.msg.ObjectList;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.process.SlcExecutionStep;
import org.argeo.slc.runtime.SlcAgent;
import org.argeo.slc.runtime.SlcAgentFactory;
import org.argeo.slc.services.SlcExecutionService;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.xml.transform.StringSource;

@Controller
public class ProcessController {

	public final static String KEY_ANSWER = "__answer";
	private final static Log log = LogFactory.getLog(ProcessController.class);

	private SlcExecutionDao slcExecutionDao;
	private SlcAgentFactory agentFactory;
	private Unmarshaller unmarshaller;
	private Marshaller marshaller;
	private SlcExecutionService slcExecutionService;
	private AttachmentsStorage attachmentsStorage;

	private SlcExecutionManager slcExecutionManager;

	@RequestMapping("/listSlcExecutions.service")
	protected ObjectList listSlcExecutions() {
		List<SlcExecution> list = slcExecutionDao.listSlcExecutions();
		return new ObjectList(list);
	}

	@RequestMapping("/getExecutionDescriptor.service")
	protected ExecutionModuleDescriptor getExecutionDescriptor(
			@RequestParam String agentId, @RequestParam String moduleName,
			@RequestParam String version) {

		SlcAgent slcAgent = agentFactory.getAgent(agentId);

		ExecutionModuleDescriptor md = slcAgent.getExecutionModuleDescriptor(
				moduleName, version);
		return md;
	}

	@RequestMapping("/listModulesDescriptors.service")
	protected ObjectList listModulesDescriptors(@RequestParam String agentId) {
		// TODO: use centralized agentId property (from MsgConstants)?
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
		return new ObjectList(set);
	}

	@RequestMapping("/getSlcExecution.service")
	protected SlcExecution getSlcExecution(@RequestParam String uuid) {
		SlcExecution slcExecution = slcExecutionDao.getSlcExecution(uuid);
		initializeSEM();
		slcExecutionManager.retrieveRealizedFlows(slcExecution);
		return slcExecution;
	}

	@RequestMapping("/newSlcExecution.service")
	protected ExecutionAnswer newSlcExecution(HttpServletRequest request,
			Model model) throws Exception {

		String agentId = request
				.getParameter(MsgConstants.PROPERTY_SLC_AGENT_ID);
		Assert.notNull(agentId, "agent id");

		String answer = request.getParameter("body");
		if (answer == null) {
			// lets read the message body instead
			BufferedReader reader = request.getReader();
			StringBuffer buffer = new StringBuffer();
			String line = null;
			while (((line = reader.readLine()) != null)) {
				buffer.append(line);
			}
			answer = buffer.toString();
		}

		if (log.isTraceEnabled())
			log.debug("Received message:\n" + answer);

		StringSource source = new StringSource(answer);
		SlcExecution slcExecution = (SlcExecution) unmarshaller
				.unmarshal(source);

		// Workaround for https://www.argeo.org/bugzilla/show_bug.cgi?id=86
		if (slcExecution.getUuid() == null
				|| slcExecution.getUuid().length() < 8)
			slcExecution.setUuid(UUID.randomUUID().toString());

		slcExecution.setStatus(SlcExecution.STATUS_SCHEDULED);
		slcExecution.getSteps().add(
				new SlcExecutionStep(SlcExecutionStep.START,
						"Process started from the Web UI"));

		initializeSEM();
		slcExecutionManager.storeRealizedFlows(slcExecution);
		slcExecutionService.newExecution(slcExecution);
		SlcAgent agent = agentFactory.getAgent(agentId);
		agent.runSlcExecution(slcExecution);

		return ExecutionAnswer.ok("Execution completed properly");
	}

	private void initializeSEM() {
		slcExecutionManager = new SlcExecutionManager(unmarshaller, marshaller,
				attachmentsStorage);
	}

	public void setSlcExecutionDao(SlcExecutionDao slcExecutionDao) {
		this.slcExecutionDao = slcExecutionDao;
	}

	public void setSlcExecutionService(SlcExecutionService slcExecutionService) {
		this.slcExecutionService = slcExecutionService;
	}

	public void setUnmarshaller(Unmarshaller unmarshaller) {
		this.unmarshaller = unmarshaller;
	}

	public void setMarshaller(Marshaller marshaller) {
		this.marshaller = marshaller;
	}

	public void setAttachmentsStorage(AttachmentsStorage attachmentsStorage) {
		this.attachmentsStorage = attachmentsStorage;
	}

	public void setAgentFactory(SlcAgentFactory agentFactory) {
		this.agentFactory = agentFactory;
	}

}
