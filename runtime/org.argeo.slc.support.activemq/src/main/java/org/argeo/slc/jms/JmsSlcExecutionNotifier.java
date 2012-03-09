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
package org.argeo.slc.jms;

import java.util.List;

import javax.jms.Destination;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.UnsupportedException;
import org.argeo.slc.execution.ExecutionProcess;
import org.argeo.slc.execution.ExecutionStep;
import org.argeo.slc.msg.process.SlcExecutionStatusRequest;
import org.argeo.slc.msg.process.SlcExecutionStepsRequest;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.process.SlcExecutionNotifier;
import org.argeo.slc.process.SlcExecutionStep;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;

@SuppressWarnings("deprecation")
public class JmsSlcExecutionNotifier implements SlcExecutionNotifier {
	private final static Log log = LogFactory
			.getLog(JmsSlcExecutionNotifier.class);

	private JmsTemplate jmsTemplate;

	private Destination executionEventDestination;

	// private Destination updateStatusDestination;

	public void updateStatus(ExecutionProcess slcExecution, String oldStatus,
			String newStatus) {
		SlcExecutionStatusRequest req = new SlcExecutionStatusRequest(
				slcExecution.getUuid(), newStatus);
		convertAndSend(req);
	}

	public void addSteps(ExecutionProcess slcExecution,
			List<ExecutionStep> additionalSteps) {
		SlcExecutionStepsRequest req = new SlcExecutionStepsRequest(
				slcExecution.getUuid(), additionalSteps);
		convertAndSend(req);
	}

	public void newExecution(SlcExecution slcExecution) {
		throw new UnsupportedException();
	}

	public void updateExecution(SlcExecution slcExecution) {
		throw new UnsupportedException();
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	public void setExecutionEventDestination(
			Destination executionEventDestination) {
		this.executionEventDestination = executionEventDestination;
	}

	protected void convertAndSend(Object req) {
		try {
			jmsTemplate.convertAndSend(executionEventDestination, req);
		} catch (JmsException e) {
			log.warn("Send request " + req.getClass() + " to server: "
					+ e.getMessage());
			if (log.isTraceEnabled())
				log.debug("Original error.", e);
		}
	}
}
