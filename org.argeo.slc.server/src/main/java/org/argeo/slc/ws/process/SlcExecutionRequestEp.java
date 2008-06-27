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
			SlcExecution slcExecution = msg.getSlcExecution();

			if (slcExecutionDao.getSlcExecution(slcExecution.getUuid()) == null) {
				if (log.isDebugEnabled())
					log.debug("Creating SLC execution #"
							+ slcExecution.getUuid());

				slcExecutionDao.create(slcExecution);
			} else {
				if (log.isDebugEnabled())
					log.debug("Updating SLC execution #"
							+ slcExecution.getUuid());

				slcExecutionDao.update(slcExecution);
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
			slcExecutionDao.update(slcExecution);
			return null;
		} else {
			throw new SlcException("Unrecognized request format: "
					+ requestObject.getClass());
		}
	}

}
