package org.argeo.slc.lib.detached;

import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.detached.DetachedAnswer;
import org.argeo.slc.detached.DetachedClient;
import org.argeo.slc.detached.DetachedRequest;
import org.argeo.slc.execution.Executable;

public class DetachedAdminTask implements Executable {
	private final static Log log = LogFactory.getLog(DetachedAdminTask.class);

	private String action;
	private DetachedClient client;
	private Properties properties;

	public void execute() {
		// Prepare and send request
		DetachedRequest request = new DetachedRequest(UUID.randomUUID()
				.toString());
		request.setRef(action);

		if (properties != null) {
			request.setProperties(properties);
		}

		try {
			client.sendRequest(request);
			DetachedAnswer answer = client.receiveAnswer();
			if (answer.getStatus() == DetachedAnswer.ERROR)
				throw new SlcException("Error when executing request "
						+ answer.getUuid() + ": " + answer.getLog());
			else
				log.info("Admin answer: " + answer.getLog());
		} catch (Exception e) {
			throw new SlcException("Could not send request.", e);
		}
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setClient(DetachedClient detachedClient) {
		this.client = detachedClient;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

}
