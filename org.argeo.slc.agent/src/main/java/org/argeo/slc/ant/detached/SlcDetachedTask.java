package org.argeo.slc.ant.detached;

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.ant.structure.SAwareTask;
import org.argeo.slc.core.SlcException;
import org.argeo.slc.detached.DetachedAnswer;
import org.argeo.slc.detached.DetachedClient;
import org.argeo.slc.detached.DetachedRequest;

public class SlcDetachedTask extends SAwareTask {
	private final static Log log = LogFactory.getLog(SlcDetachedTask.class);

	private String client;
	private String action;

	@Override
	protected void executeActions(String mode) {
		DetachedClient detachedClient = getBean(client);
		DetachedRequest request = new DetachedRequest(UUID.randomUUID()
				.toString());
		request.setRef(action);
		try {
			detachedClient.sendRequest(request);
			DetachedAnswer answer = detachedClient.receiveAnswer();
			if (answer.getStatus() == DetachedAnswer.ERROR)
				throw new SlcException("Error when executing request "
						+ answer.getUuid() + ": " + answer.getLog());
			else
				log.info("Admin answer: " + answer.getLog());
		} catch (Exception e) {
			throw new SlcException("Could not send request.", e);
		}
	}

	public void setClient(String driverBean) {
		this.client = driverBean;
	}

	public void setAction(String action) {
		this.action = action;
	}

}
