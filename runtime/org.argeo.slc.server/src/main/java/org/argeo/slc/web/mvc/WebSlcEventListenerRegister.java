package org.argeo.slc.web.mvc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.argeo.slc.msg.event.SlcEventListenerDescriptor;
import org.argeo.slc.msg.event.SlcEventListenerRegister;
import org.springframework.web.context.request.RequestContextHolder;

public class WebSlcEventListenerRegister implements SlcEventListenerRegister,
		Serializable {
	public final static String ATTR_EVENT_LISTENER = "slcEventListener";

	static final long serialVersionUID = 1l;

	//private String clientId = UUID.randomUUID().toString();

	/** Synchronized */
	private List<SlcEventListenerDescriptor> descriptors = new Vector<SlcEventListenerDescriptor>();

	public synchronized void addEventListenerDescriptor(
			SlcEventListenerDescriptor eventListenerDescriptor) {
		if (descriptors.contains(eventListenerDescriptor))
			descriptors.remove(eventListenerDescriptor);
		descriptors.add(eventListenerDescriptor);
	}

	public synchronized void removeEventListenerDescriptor(
			SlcEventListenerDescriptor eventListenerDescriptor) {
		descriptors.remove(eventListenerDescriptor);
	}

	public synchronized List<SlcEventListenerDescriptor> getDescriptorsCopy() {
		return new ArrayList<SlcEventListenerDescriptor>(descriptors);
	}

	// public SlcEvent listen(SlcEventListener eventListener, Long timeout) {
	// return eventListener.listen(clientId, getDescriptorsCopy(), timeout);
	// }

	// public void init() {
	// clientId = getSessionId();
	// checkClientId();
	//
	// if (log.isDebugEnabled())
	// log.debug("Initialized web event listener " + clientId);
	// }
	//
	// public void close() {
	// checkClientId();
	// if (log.isDebugEnabled())
	// log.debug("Closed web event listener " + clientId);
	// }

	// protected void checkClientId() {
	// String sessionId = getSessionId();
	// if (clientId == null || !clientId.equals(sessionId))
	// throw new SlcException("Client id " + clientId
	// + " not consistent with web session id " + sessionId);
	// }
	//
	// protected String getSessionId() {
	// return RequestContextHolder.currentRequestAttributes().getSessionId();
	// }
	//
	public String getId() {
		return RequestContextHolder.currentRequestAttributes().getSessionId();
	}

}
