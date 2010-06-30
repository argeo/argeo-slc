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

package org.argeo.slc.web.mvc.event;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.msg.event.SlcEvent;
import org.argeo.slc.msg.event.SlcEventListener;
import org.argeo.slc.msg.event.SlcEventListenerRegister;
import org.argeo.slc.web.mvc.AbstractServiceController;
import org.springframework.web.servlet.ModelAndView;

public class PollEventController extends AbstractServiceController {
	private final static Log log = LogFactory.getLog(PollEventController.class);

	private SlcEventListener eventListener = null;

	private SlcEventListenerRegister eventListenerRegister;
	private Long defaultTimeout = 10000l;

	@Override
	protected void handleServiceRequest(HttpServletRequest request,
			HttpServletResponse response, ModelAndView modelAndView)
			throws Exception {
		String timeoutStr = request.getParameter("timeout");

		final Long timeout;
		if (timeoutStr != null)
			timeout = Long.parseLong(timeoutStr);
		else
			timeout = defaultTimeout;

		SlcEvent event = eventListener.listen(eventListenerRegister.getId(),
				eventListenerRegister.getDescriptorsCopy(), timeout);
		if (event != null) {
			modelAndView.addObject("event", event);

			if (log.isTraceEnabled())
				log.debug("Received event: "
						+ event.getHeaders().get(SlcEvent.EVENT_TYPE));
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
