package org.argeo.slc.web.mvc.event;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.argeo.slc.msg.event.SlcEvent;
import org.argeo.slc.msg.event.SlcEventListenerDescriptor;
import org.argeo.slc.msg.event.SlcEventListenerRegister;
import org.argeo.slc.web.mvc.AbstractServiceController;
import org.springframework.web.servlet.ModelAndView;

public class AddEventListenerController extends AbstractServiceController {

	private SlcEventListenerRegister eventListenerRegister;

	@Override
	protected void handleServiceRequest(HttpServletRequest request,
			HttpServletResponse response, ModelAndView modelAndView)
			throws Exception {
		String eventType = request.getParameter(SlcEvent.EVENT_TYPE);
		String eventFilter = request.getParameter(SlcEvent.EVENT_FILTER);

		eventListenerRegister
				.addEventListenerDescriptor(new SlcEventListenerDescriptor(
						eventType, eventFilter));
	}

	public void setEventListenerRegister(
			SlcEventListenerRegister eventListenerRegister) {
		this.eventListenerRegister = eventListenerRegister;
	}

}
