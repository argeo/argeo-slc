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
			SlcExecution slcExecution = slcExecutionDao.getSlcExecution(uuid);

			if (slcExecution == null)
				throw new SlcException("Could not find slc execution " + uuid);

			List<SlcExecutionStep> additionalSteps = msg.getSteps();
			if(log.isTraceEnabled()){
				log.trace("Trying to add additional steps to slc execution "+uuid+":");
				for(SlcExecutionStep step: additionalSteps){
					log.trace("Step "+step.getUuid()+" (in slc execution "+uuid+")");
				}
			}
			slcExecution.getSteps().addAll(additionalSteps);

			slcExecutionDao.update(slcExecution);
			log.debug("Added " + msg.getSteps().size()
					+ " steps to SlcExecution with uuid "
					+ slcExecution.getUuid());
			return null;
		} catch (Exception e) {
			log.error("Could not update SlcExecution " + uuid
					+ " with additional steps", e);
			throw e;
		}
	}

}
