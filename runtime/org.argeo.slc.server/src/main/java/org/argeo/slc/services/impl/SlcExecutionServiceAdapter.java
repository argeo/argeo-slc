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

package org.argeo.slc.services.impl;

import java.util.List;

import org.argeo.slc.UnsupportedException;
import org.argeo.slc.msg.process.SlcExecutionStatusRequest;
import org.argeo.slc.msg.process.SlcExecutionStepsRequest;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.process.SlcExecutionNotifier;
import org.argeo.slc.process.SlcExecutionStep;
import org.argeo.slc.services.SlcExecutionService;

/** In memory bridge between SLC execution notifier and service. */
public class SlcExecutionServiceAdapter implements SlcExecutionNotifier {
	private SlcExecutionService slcExecutionService;

	public void updateStatus(SlcExecution slcExecution, String oldStatus,
			String newStatus) {
		SlcExecutionStatusRequest req = new SlcExecutionStatusRequest(
				slcExecution.getUuid(), newStatus);
		slcExecutionService.updateStatus(req);
	}

	public void addSteps(SlcExecution slcExecution,
			List<SlcExecutionStep> additionalSteps) {
		SlcExecutionStepsRequest req = new SlcExecutionStepsRequest(
				slcExecution.getUuid(), additionalSteps);
		slcExecutionService.addSteps(req);
	}

	public void newExecution(SlcExecution slcExecution) {
		throw new UnsupportedException();
		//slcExecutionService.newExecution(slcExecution);
	}

	public void updateExecution(SlcExecution slcExecution) {
		throw new UnsupportedException();
	}

	public void setSlcExecutionService(SlcExecutionService slcExecutionService) {
		this.slcExecutionService = slcExecutionService;
	}

}
