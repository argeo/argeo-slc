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
package org.argeo.slc.web.mvc.controllers;

import java.io.BufferedReader;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
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

		slcExecution.setStatus(SlcExecution.SCHEDULED);
		slcExecution.getSteps().add(
				new SlcExecutionStep(SlcExecutionStep.START,
						"Process started from the Web UI"));

		initializeSEM();
		slcExecutionManager.storeRealizedFlows(slcExecution);
		slcExecutionService.newExecution(slcExecution);
		SlcAgent agent = agentFactory.getAgent(agentId);
		//agent.runSlcExecution(slcExecution);

		return ExecutionAnswer.ok("Execution completed properly");
	}

	@RequestMapping("/tailSlcExecutionStepsCount.service")
	protected ObjectList tailSlcExecutionSteps(@RequestParam String uuid,
			@RequestParam Integer stepCount) {
		List<SlcExecutionStep> list = slcExecutionDao
				.tailSteps(uuid, stepCount);
		return new ObjectList(list);
	}

	@RequestMapping("/tailSlcExecutionStepsOffset.service")
	protected ObjectList tailSlcExecutionSteps(@RequestParam String uuid,
			@RequestParam String stepOffsetUuid) {
		List<SlcExecutionStep> list = slcExecutionDao.tailSteps(uuid,
				stepOffsetUuid);
		return new ObjectList(list);
	}

	@RequestMapping("/downloadSlcExecution.service")
	protected void downloadSlcExecution(@RequestParam String uuid,
			@RequestParam String ext, HttpServletResponse response)
			throws Exception {
		String contentType;
		// cf. http://en.wikipedia.org/wikServicei/Internet_media_type
		if ("csv".equals(ext))
			contentType = "text/csv";
		else if ("pdf".equals(ext))
			contentType = "application/pdf";
		else if ("zip".equals(ext))
			contentType = "application/zip";
		else if ("html".equals(ext))
			contentType = "application/html";
		else if ("txt".equals(ext) || "log".equals(ext))
			contentType = "text/plain";
		else if ("doc".equals(ext) || "docx".equals(ext))
			contentType = "application/msword";
		else if ("xls".equals(ext) || "xlsx".equals(ext))
			contentType = "application/vnd.ms-excel";
		else if ("xml".equals(ext))
			contentType = "text/xml";
		else
			contentType = "Content-Type: application/force-download";

		String name = "Process-" + uuid + "." + ext;

		SlcExecution process = slcExecutionDao.getSlcExecution(uuid);

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

		// TODO: put it in a separate view
		if ("log".equals(ext)) {
			StringBuffer buf = new StringBuffer("");
			buf.append("#\n# PROCESS " + process.getUuid() + "\n#\n\n");
			buf
					.append("Started at " + df.format(process.getStartDate())
							+ "\n");
			buf.append("Ended at " + df.format(process.getEndDate()) + "\n");
			buf.append("On host " + process.getHost() + "\n");
			buf.append("\n# LOG\n\n");
			for (SlcExecutionStep step : process.getSteps()) {
				buf.append(df.format(step.getTimestamp()));
				buf.append(" ");
				for (int i = 0; i < step.getLogLines().size(); i++) {
					if (i > 0)
						buf.append('\n');
					buf.append(step.getLogLines().get(i));
				}
				buf.append(" - ");
				buf.append(step.getType());
				buf.append(" - ");
				buf.append('[').append(step.getThread()).append(']');
				buf.append('\n');
			}
			prepareDownloadResponse(name, contentType, response);
			response.getWriter().print(buf);
		} else {
			throw new SlcException("Unsupported content type " + contentType);
		}
	}

	protected void prepareDownloadResponse(String name, String contentType,
			HttpServletResponse response) {
		response.setHeader("Content-Disposition", "attachment; filename=\""
				+ name + "\"");
		response.setContentType(contentType + ";name=\"" + name + "\"");
		response.setHeader("Expires", "0");
		response.setHeader("Cache-Control", "no-cache, must-revalidate");
		response.setHeader("Pragma", "no-cache");
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
