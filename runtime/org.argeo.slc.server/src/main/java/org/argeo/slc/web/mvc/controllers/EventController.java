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

package org.argeo.slc.web.mvc.controllers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.msg.ExecutionAnswer;
import org.argeo.slc.msg.event.SlcEvent;
import org.argeo.slc.msg.event.SlcEventListener;
import org.argeo.slc.msg.event.SlcEventListenerDescriptor;
import org.argeo.slc.msg.event.SlcEventListenerRegister;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EventController {

	private final static Log log = LogFactory.getLog(EventController.class);

	private Long defaultTimeout = 10000l;

	// IoC
	private SlcEventListener eventListener = null;
	// the following bean as a Session scope.
	private SlcEventListenerRegister eventListenerRegister;

	// Business Methods
	@RequestMapping("/addEventListener.service")
	public ExecutionAnswer addEventListener(
			@RequestParam(SlcEvent.EVENT_TYPE) String eventType,
			@RequestParam(value = SlcEvent.EVENT_FILTER, required = false) String eventFilter) {

		eventListenerRegister
				.addEventListenerDescriptor(new SlcEventListenerDescriptor(
						eventType, eventFilter));
		if (log.isTraceEnabled()) {
			log.trace("Registered listener on register "
					+ eventListenerRegister.getId() + " for type " + eventType
					+ ", filter=" + eventFilter);
			log.trace("Nb of registered descriptors : "
					+ eventListenerRegister.getDescriptorsCopy().size());
		}
		return ExecutionAnswer.ok("Execution completed properly");

	}

	@RequestMapping("/removeEventListener.service")
	public ExecutionAnswer removeEventListener(
			@RequestParam(SlcEvent.EVENT_TYPE) String eventType,
			@RequestParam(value = SlcEvent.EVENT_FILTER, required = false) String eventFilter) {

		eventListenerRegister
				.removeEventListenerDescriptor(new SlcEventListenerDescriptor(
						eventType, eventFilter));
		if (log.isTraceEnabled()) {
			log.trace("Removed listener from register "
					+ eventListenerRegister.getId() + " for type " + eventType
					+ ", filter=" + eventFilter);
			log.trace("Nb of registered descriptors : "
					+ eventListenerRegister.getDescriptorsCopy().size());
		}
		return ExecutionAnswer.ok("Execution completed properly");
	}

	@RequestMapping("/pollEvent.service")
	public Object pollEvent(
			@RequestParam(value = "timeout", required = false) String timeoutStr) {
		final Long timeout;
		if (timeoutStr != null)
			timeout = Long.parseLong(timeoutStr);
		else
			timeout = defaultTimeout;
		if (log.isTraceEnabled()) {
			log.trace("Begin poolEvent.service :"
					+ " Nb of registered descriptors : "
					+ eventListenerRegister.getDescriptorsCopy().size());
		}
		SlcEvent event = eventListener.listen(eventListenerRegister.getId(),
				eventListenerRegister.getDescriptorsCopy(), timeout);
		if (event != null) {
			if (log.isTraceEnabled())
				log.trace("Event heard : " + event.toString());
			return event;
		} else {
			if (log.isTraceEnabled())
				log.trace("No Event heard - Time out: ");
			return ExecutionAnswer.ok("Execution completed properly");
		}

	}

	public void setEventListenerRegister(
			SlcEventListenerRegister eventListenerRegister) {
		this.eventListenerRegister = eventListenerRegister;
	}

	public void setDefaultTimeout(Long defaultTimeout) {
		this.defaultTimeout = defaultTimeout;
	}

	public void setEventListener(SlcEventListener eventListener) {
		this.eventListener = eventListener;
	}
}
