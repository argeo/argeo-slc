package org.argeo.slc.services.impl;

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

		if (msg.getNewStatus().equals(SlcExecution.STATUS_FINISHED))
			slcExecution.getSteps().add(
					new SlcExecutionStep(SlcExecutionStep.TYPE_END,
							"Process finished."));

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