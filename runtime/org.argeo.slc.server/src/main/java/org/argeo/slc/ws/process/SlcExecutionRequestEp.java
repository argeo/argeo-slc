package org.argeo.slc.ws.process;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.argeo.slc.core.SlcException;
import org.argeo.slc.core.process.SlcExecution;
import org.argeo.slc.dao.process.SlcExecutionDao;
import org.argeo.slc.msg.process.SlcExecutionRequest;
import org.argeo.slc.msg.process.SlcExecutionStatusRequest;

import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

public class SlcExecutionRequestEp extends AbstractMarshallingPayloadEndpoint {

	private Log log = LogFactory.getLog(getClass());

	private final SlcExecutionDao slcExecutionDao;

	public SlcExecutionRequestEp(SlcExecutionDao slcExecutionDao) {
		this.slcExecutionDao = slcExecutionDao;
	}

	@Override
	protected Object invokeInternal(Object requestObject) throws Exception {
		if (requestObject instanceof SlcExecutionRequest) {

			SlcExecutionRequest msg = (SlcExecutionRequest) requestObject;
			SlcExecution slcExecutionMsg = msg.getSlcExecution();

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
			return null;

		} else if (requestObject instanceof SlcExecutionStatusRequest) {
			SlcExecutionStatusRequest msg = (SlcExecutionStatusRequest) requestObject;
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
			return null;
		} else {
			throw new SlcException("Unrecognized request format: "
					+ requestObject.getClass());
		}
	}
}
