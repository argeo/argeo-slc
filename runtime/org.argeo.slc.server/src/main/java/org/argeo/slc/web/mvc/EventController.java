package org.argeo.slc.web.mvc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.msg.ExecutionAnswer;
import org.argeo.slc.msg.event.SlcEvent;
import org.argeo.slc.msg.event.SlcEventListener;
import org.argeo.slc.msg.event.SlcEventListenerDescriptor;
import org.argeo.slc.msg.event.SlcEventListenerRegister;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EventController {

	private final static Log log = LogFactory.getLog(EventController.class);

	public final static String KEY_ANSWER = "__answer";
	private Long defaultTimeout = 10000l;

	// IoC
	private SlcEventListenerRegister eventListenerRegister;
	private SlcEventListener eventListener = null;

	public EventController() {
		if (log.isDebugEnabled())
			log.debug("In EventController Constructor");
	}

	// Business Methods

	@RequestMapping("/addEventListener.service")
	public String addEventListener(
			@RequestParam(SlcEvent.EVENT_TYPE) String eventType,
			@RequestParam(value=SlcEvent.EVENT_FILTER, required=false) String eventFilter, Model model) {

		eventListenerRegister
				.addEventListenerDescriptor(new SlcEventListenerDescriptor(
						eventType, eventFilter));
		if (log.isTraceEnabled())
			log.trace("Registered listener on register "
					+ eventListenerRegister.getId() + " for type " + eventType
					+ ", filter=" + eventFilter);
		model.addAttribute(KEY_ANSWER, ExecutionAnswer
				.ok("Execution completed properly"));
		return KEY_ANSWER;

	}

	@RequestMapping("/removeEventListener.service")
	public String removeEventListener(
			@RequestParam(SlcEvent.EVENT_TYPE) String eventType,
			@RequestParam(value=SlcEvent.EVENT_FILTER, required=false) String eventFilter, Model model) {

		eventListenerRegister
				.removeEventListenerDescriptor(new SlcEventListenerDescriptor(
						eventType, eventFilter));
		if (log.isTraceEnabled())
			log.trace("Removed listener from register "
					+ eventListenerRegister.getId() + " for type " + eventType
					+ ", filter=" + eventFilter);
		model.addAttribute(KEY_ANSWER, ExecutionAnswer
				.ok("Execution completed properly"));
		return KEY_ANSWER;
	}

	@RequestMapping("/pollEvent.service")
	public String pollEvent(@RequestParam("timeout") String timeoutStr,
			Model model) {
		final Long timeout;
		if (timeoutStr != null)
			timeout = Long.parseLong(timeoutStr);
		else
			timeout = defaultTimeout;

		SlcEvent event = eventListener.listen(eventListenerRegister.getId(),
				eventListenerRegister.getDescriptorsCopy(), timeout);
		if (event != null) {
			model.addAttribute("event", event);
			if (log.isTraceEnabled())
				log.debug("Received event: "
						+ event.getHeaders().get(SlcEvent.EVENT_TYPE));
		}
		model.addAttribute(KEY_ANSWER, ExecutionAnswer
				.ok("Execution completed properly"));
		return KEY_ANSWER;
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
