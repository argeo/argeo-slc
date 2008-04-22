package org.argeo.slc.ws.process;

import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.argeo.slc.core.process.SlcExecution;
import org.argeo.slc.dao.process.SlcExecutionDao;
import org.argeo.slc.msg.process.SlcExecutionStepsRequest;

public class SlcExecutionStepsRequestEp extends
		AbstractMarshallingPayloadEndpoint {

	private Log log = LogFactory.getLog(getClass());

	private final SlcExecutionDao slcExecutionDao;

	public SlcExecutionStepsRequestEp(SlcExecutionDao slcExecutionDao) {
		this.slcExecutionDao = slcExecutionDao;
	}

	@Override
	protected Object invokeInternal(Object requestObject) throws Exception {
		SlcExecutionStepsRequest msg = (SlcExecutionStepsRequest) requestObject;
		String uuid = msg.getSlcExecutionUuid();
		SlcExecution slcExecution = slcExecutionDao.getSlcExecution(uuid);

		slcExecution.getSteps().addAll(msg.getSteps());

		slcExecutionDao.update(slcExecution);
		log.debug("Added " + msg.getSteps().size()
				+ "steps to SlcExecution with uuid " + slcExecution.getUuid());
		return null;
	}

}
