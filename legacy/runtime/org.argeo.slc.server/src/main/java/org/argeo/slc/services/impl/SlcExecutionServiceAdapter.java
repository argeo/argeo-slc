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
package org.argeo.slc.services.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.execution.ExecutionProcess;
import org.argeo.slc.execution.ExecutionStep;
import org.argeo.slc.msg.process.SlcExecutionStatusRequest;
import org.argeo.slc.msg.process.SlcExecutionStepsRequest;
import org.argeo.slc.process.SlcExecutionNotifier;
import org.argeo.slc.process.SlcExecutionStep;
import org.argeo.slc.services.SlcExecutionService;

/** In memory bridge between SLC execution notifier and service. */
@SuppressWarnings("deprecation")
public class SlcExecutionServiceAdapter implements SlcExecutionNotifier {
	private final static Log log = LogFactory
			.getLog(SlcExecutionServiceAdapter.class);

	private SlcExecutionService slcExecutionService;

	public void updateStatus(ExecutionProcess slcExecution, String oldStatus,
			String newStatus) {
		SlcExecutionStatusRequest req = new SlcExecutionStatusRequest(
				slcExecution.getUuid(), newStatus);
		try {
			slcExecutionService.updateStatus(req);
		} catch (Exception e) {
			log.trace("Cannot update process status " + e);
		}
	}

	public void addSteps(ExecutionProcess slcExecution,
			List<ExecutionStep> additionalSteps) {
		SlcExecutionStepsRequest req = new SlcExecutionStepsRequest(
				slcExecution.getUuid(), additionalSteps);
		try {
			slcExecutionService.addSteps(req);
		} catch (Exception e) {
			log.trace("Cannot add steps " + e);
		}
	}

	public void setSlcExecutionService(SlcExecutionService slcExecutionService) {
		this.slcExecutionService = slcExecutionService;
	}

}
