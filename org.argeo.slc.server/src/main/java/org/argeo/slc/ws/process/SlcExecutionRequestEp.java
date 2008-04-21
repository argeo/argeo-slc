package org.argeo.slc.ws.process;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.core.process.SlcExecution;
import org.argeo.slc.msg.process.SlcExecutionRequest;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

public class SlcExecutionRequestEp extends
		AbstractMarshallingPayloadEndpoint {

	private Log log = LogFactory.getLog(getClass());

	@Override
	protected Object invokeInternal(Object requestObject) throws Exception {
		SlcExecutionRequest msg = (SlcExecutionRequest) requestObject;
		SlcExecution slcExecution = msg.getSlcExecution();
		log.info("Received save or update request fro SlcExecution "
				+ slcExecution.getUuid());
		return null;
	}

}
