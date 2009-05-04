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

	private SlcEventListener eventListener;
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

		SlcEvent event = eventListener.listen(eventListenerRegister, timeout);
		if (event != null) {
			modelAndView.addObject("event", event);

			if (log.isTraceEnabled())
				log.debug("Received event: "
						+ event.getHeaders().get(SlcEvent.EVENT_TYPE));
		}
	}

	public void setEventListener(SlcEventListener slcEventListener) {
		this.eventListener = slcEventListener;
	}

	public void setEventListenerRegister(
			SlcEventListenerRegister eventListenerRegister) {
		this.eventListenerRegister = eventListenerRegister;
	}

	public void setDefaultTimeout(Long defaultTimeout) {
		this.defaultTimeout = defaultTimeout;
	}

}
