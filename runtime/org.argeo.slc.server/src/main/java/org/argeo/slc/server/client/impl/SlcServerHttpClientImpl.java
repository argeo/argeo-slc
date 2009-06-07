package org.argeo.slc.server.client.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.Condition;
import org.argeo.slc.msg.ObjectList;
import org.argeo.slc.runtime.SlcAgentDescriptor;
import org.argeo.slc.server.client.SlcServerHttpClient;

public class SlcServerHttpClientImpl extends AbstractHttpServicesClient
		implements SlcServerHttpClient {
	public final static String LIST_AGENTS = "listAgents.service";

	private final static Log log = LogFactory
			.getLog(SlcServerHttpClientImpl.class);

	private Long retryTimeout = 60 * 1000l;

	public SlcAgentDescriptor waitForOneAgent() {
		ObjectList objectList = callServiceSafe(LIST_AGENTS, null,
				new Condition<ObjectList>() {
					public Boolean check(ObjectList obj) {
						int size = obj.getObjects().size();
						if (log.isTraceEnabled())
							log.trace("Object list size: " + size);
						return size == 1;
					}
				}, retryTimeout);
		return (SlcAgentDescriptor) objectList.getObjects().get(0);
	}

	/** Timeout in ms after which a safe call will throw an exception. */
	public void setRetryTimeout(Long retryTimeout) {
		this.retryTimeout = retryTimeout;
	}

}
