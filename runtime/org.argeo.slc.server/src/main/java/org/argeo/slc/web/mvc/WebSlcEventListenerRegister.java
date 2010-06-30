/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
