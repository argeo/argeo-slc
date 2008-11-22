package org.argeo.slc.ant.detached;

import java.util.Properties;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.ant.spring.MapArg;
import org.argeo.slc.ant.structure.SAwareTask;
import org.argeo.slc.detached.DetachedAnswer;
import org.argeo.slc.detached.DetachedClient;
import org.argeo.slc.detached.DetachedRequest;
import org.argeo.slc.spring.SpringUtils;

public class SlcDetachedTask extends SAwareTask {
	private final static Log log = LogFactory.getLog(SlcDetachedTask.class);

	private String client;
	private String action;

	private MapArg properties;

	@Override
	protected void executeActions(String mode) {
		// Find detached client
		DetachedClient detachedClient = null;
		if (client != null)
			detachedClient = getBean(client);
		else
			detachedClient = SpringUtils.loadSingleFromContext(getContext(),
					DetachedClient.class);

		if (detachedClient == null)
			throw new SlcException("Could not find any detached client.");

		// Prepare and send request
		DetachedRequest request = new DetachedRequest(UUID.randomUUID()
				.toString());
		request.setRef(action);

		if (properties != null) {
			Properties props = new Properties();
			props.putAll(properties.getMap());
			request.setProperties(props);
		}

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

	public MapArg createProperties() {
		if (properties == null)
			properties = new MapArg();
		else
			throw new SlcException("Properties already declared.");
		return properties;
	}
}
