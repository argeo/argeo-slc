package org.argeo.slc.ws.process;

import java.util.List;

import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.argeo.slc.core.SlcException;
import org.argeo.slc.core.process.SlcExecution;
import org.argeo.slc.core.process.SlcExecutionStep;
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
		String uuid = null;
		try {
			SlcExecutionStepsRequest msg = (SlcExecutionStepsRequest) requestObject;
			uuid = msg.getSlcExecutionUuid();
			if (log.isTraceEnabled()) {
				log.trace("Trying to add additional steps to SLC execution #"
						+ uuid + ":");
				for (SlcExecutionStep step : msg.getSteps()) {
					log.trace("Step " + step.getUuid() + " (in SLC execution #"
							+ uuid + ")");
				}
			}

			log.debug("Adding " + msg.getSteps().size()
					+ " steps to SLC execution #" + uuid);

			slcExecutionDao.addSteps(uuid, msg.getSteps());
			return null;
		} catch (Exception e) {
			log.error("Could not update SLC execution #" + uuid
					+ " with additional steps", e);
			throw e;
		}
	}

}
