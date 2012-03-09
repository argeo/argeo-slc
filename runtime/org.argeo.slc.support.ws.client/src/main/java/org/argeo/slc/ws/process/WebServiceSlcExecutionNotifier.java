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
package org.argeo.slc.ws.process;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.msg.process.SlcExecutionRequest;
import org.argeo.slc.msg.process.SlcExecutionStatusRequest;
import org.argeo.slc.msg.process.SlcExecutionStepsRequest;
import org.argeo.slc.execution.ExecutionStep;
import org.argeo.slc.execution.ExecutionProcess;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.process.SlcExecutionNotifier;
import org.argeo.slc.process.SlcExecutionStep;
import org.argeo.slc.ws.client.WebServiceUtils;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;

public class WebServiceSlcExecutionNotifier implements SlcExecutionNotifier {
	private WebServiceTemplate template;

	private Log log = LogFactory.getLog(getClass());

	private Boolean cannotConnect = false;

	public void newExecution(ExecutionProcess process) {
		SlcExecution slcExecution = (SlcExecution) process;
		if (cannotConnect)
			return;

		SlcExecutionRequest req = new SlcExecutionRequest();
		req.setSlcExecution(slcExecution);
		try {
			WebServiceUtils.marshalSendAndReceive(template, req);
			if (log.isTraceEnabled())
				log.trace("Notified creation of slc execution "
						+ slcExecution.getUuid());
		} catch (SoapFaultClientException e) {
			WebServiceUtils.manageSoapException(e);
		} catch (WebServiceIOException e) {
			manageIoException(e);
		}
	}

	public void updateExecution(SlcExecution slcExecution) {
		if (cannotConnect)
			return;

		SlcExecutionRequest req = new SlcExecutionRequest();
		req.setSlcExecution(slcExecution);
		try {
			WebServiceUtils.marshalSendAndReceive(template, req);
			if (log.isTraceEnabled())
				log.trace("Notified update of slc execution "
						+ slcExecution.getUuid());
		} catch (SoapFaultClientException e) {
			WebServiceUtils.manageSoapException(e);
		} catch (WebServiceIOException e) {
			manageIoException(e);
		}
	}

	public void updateStatus(ExecutionProcess process, String oldStatus,
			String newStatus) {
		SlcExecution slcExecution = (SlcExecution) process;
		if (cannotConnect)
			return;

		SlcExecutionStatusRequest req = new SlcExecutionStatusRequest(
				slcExecution.getUuid(), newStatus);
		try {
			WebServiceUtils.marshalSendAndReceive(template, req);
			if (log.isTraceEnabled())
				log.trace("Notified status update of slc execution "
						+ slcExecution.getUuid());
		} catch (SoapFaultClientException e) {
			WebServiceUtils.manageSoapException(e);
		} catch (WebServiceIOException e) {
			manageIoException(e);
		}
	}

	public void addSteps(ExecutionProcess process,
			List<ExecutionStep> additionalSteps) {
		SlcExecution slcExecution = (SlcExecution) process;
		if (cannotConnect)
			return;

		SlcExecutionStepsRequest req = new SlcExecutionStepsRequest(
				slcExecution.getUuid(), additionalSteps);
		try {
			WebServiceUtils.marshalSendAndReceive(template, req);
			if (log.isTraceEnabled())
				log.trace("Added steps to slc execution "
						+ slcExecution.getUuid());
		} catch (SoapFaultClientException e) {
			WebServiceUtils.manageSoapException(e);
		} catch (WebServiceIOException e) {
			manageIoException(e);
		}
	}

	public void setTemplate(WebServiceTemplate template) {
		this.template = template;
	}

	protected void manageIoException(WebServiceIOException e) {
		if (!cannotConnect) {
			log.error("Cannot connect to " + template.getDefaultUri()
					+ ". Won't try again.", e);
			cannotConnect = true;
		}
	}

}
