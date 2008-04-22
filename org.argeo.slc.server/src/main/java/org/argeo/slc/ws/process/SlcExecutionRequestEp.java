package org.argeo.slc.ws.process;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.core.process.SlcExecution;
import org.argeo.slc.dao.process.SlcExecutionDao;
import org.argeo.slc.msg.process.SlcExecutionRequest;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

public class SlcExecutionRequestEp extends AbstractMarshallingPayloadEndpoint {

	private Log log = LogFactory.getLog(getClass());

	private final SlcExecutionDao slcExecutionDao;

	public SlcExecutionRequestEp(SlcExecutionDao slcExecutionDao) {
		this.slcExecutionDao = slcExecutionDao;
	}

	@Override
	protected Object invokeInternal(Object requestObject) throws Exception {
		SlcExecutionRequest msg = (SlcExecutionRequest) requestObject;
		SlcExecution slcExecution = msg.getSlcExecution();

		if (slcExecutionDao.getSlcExecution(slcExecution.getUuid()) == null) {
			slcExecutionDao.create(slcExecution);
			log.debug("Created SlcExecution with uuid "
					+ slcExecution.getUuid());
		} else {
			slcExecutionDao.update(slcExecution);
			log.debug("Updated SlcExecution with uuid "
					+ slcExecution.getUuid());
		}
		return null;
	}

}
