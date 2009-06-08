package org.argeo.slc.services.impl.process;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.dao.process.SlcExecutionDao;
import org.argeo.slc.msg.process.SlcExecutionStatusRequest;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.services.process.SlcExecutionService;

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
			if (log.isTraceEnabled())
				log.trace("Updating SLC execution #"
						+ slcExecutionMsg.getUuid());

			slcExecutionDao.merge(slcExecutionMsg);
		}
	}

	public void updateStatus(SlcExecutionStatusRequest msg) {
		SlcExecution slcExecution = slcExecutionDao.getSlcExecution(msg
				.getSlcExecutionUuid());
		if (slcExecution == null)
			throw new SlcException("Could not find SLC execution #"
					+ msg.getSlcExecutionUuid());

		slcExecution.setStatus(msg.getNewStatus());

		if (log.isTraceEnabled())
			log.trace("Updating status for SLC execution #"
					+ slcExecution.getUuid());

		slcExecutionDao.update(slcExecution);
	}
}
