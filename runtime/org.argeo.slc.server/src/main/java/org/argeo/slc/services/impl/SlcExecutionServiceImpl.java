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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.dao.process.SlcExecutionDao;
import org.argeo.slc.msg.process.SlcExecutionStatusRequest;
import org.argeo.slc.msg.process.SlcExecutionStepsRequest;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.process.SlcExecutionStep;
import org.argeo.slc.services.SlcExecutionService;

public class SlcExecutionServiceImpl implements SlcExecutionService {
	private final Log log = LogFactory.getLog(getClass());

	private final SlcExecutionDao slcExecutionDao;

	public SlcExecutionServiceImpl(SlcExecutionDao slcExecutionDao) {
		this.slcExecutionDao = slcExecutionDao;
	}

	public void newExecution(SlcExecution slcExecutionMsg) {
		SlcExecution slcExecutionPersisted = slcExecutionDao
				.getSlcExecution(slcExecutionMsg.getUuid());
		if (slcExecutionPersisted == null) {
			if (log.isTraceEnabled())
				log.trace("Creating SLC execution #"
						+ slcExecutionMsg.getUuid());

			slcExecutionDao.create(slcExecutionMsg);
		} else {
			throw new SlcException(
					"There is already an SlcExecution registered with id "
							+ slcExecutionMsg.getUuid());
			// if (log.isTraceEnabled())
			// log.trace("Updating SLC execution #"
			// + slcExecutionMsg.getUuid());
			//
			// slcExecutionDao.merge(slcExecutionMsg);
		}
	}

	public void updateStatus(SlcExecutionStatusRequest msg) {
		SlcExecution slcExecution = slcExecutionDao.getSlcExecution(msg
				.getSlcExecutionUuid());
		if (slcExecution == null)
			throw new SlcException("Could not find SLC execution #"
					+ msg.getSlcExecutionUuid());

		slcExecution.setStatus(msg.getNewStatus());

		if (msg.getNewStatus().equals(SlcExecution.STATUS_FINISHED)) {
			List<SlcExecutionStep> steps = new ArrayList<SlcExecutionStep>();
			steps.add(new SlcExecutionStep(SlcExecutionStep.END,
					"Process finished."));
			slcExecutionDao.addSteps(slcExecution.getUuid(), steps);
		}

		if (log.isTraceEnabled())
			log.trace("Updating status for SLC execution #"
					+ slcExecution.getUuid() + " to status "
					+ msg.getNewStatus());

		slcExecutionDao.update(slcExecution);
	}

	public void addSteps(SlcExecutionStepsRequest msg) {
		slcExecutionDao.addSteps(msg.getSlcExecutionUuid(), msg.getSteps());
	}

}
