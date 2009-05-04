package org.argeo.slc.services;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.core.runtime.AbstractAgent;
import org.argeo.slc.msg.event.SlcEvent;
import org.argeo.slc.msg.event.SlcEventPublisher;
import org.argeo.slc.runtime.SlcAgentDescriptor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class EventPublisherAspect {
	private final static Log log = LogFactory
			.getLog(EventPublisherAspect.class);

	private List<SlcEventPublisher> eventPublishers;

	@After("execution(void org.argeo.slc.services.runtime.AgentService.register(..))")
	public void registerAgent(JoinPoint jp) throws Throwable {
		SlcAgentDescriptor agentDescriptor = (SlcAgentDescriptor) jp.getArgs()[0];
		SlcEvent event = new SlcEvent("agentRegistered");
		event.getHeaders().put(AbstractAgent.PROPERTY_SLC_AGENT_ID,
				agentDescriptor.getUuid());
		publishEvent(event);
	}

	@After("execution(void org.argeo.slc.services.runtime.AgentService.unregister(..))")
	public void unregisterAgent(JoinPoint jp) throws Throwable {
		SlcAgentDescriptor agentDescriptor = (SlcAgentDescriptor) jp.getArgs()[0];
		SlcEvent event = new SlcEvent("agentUnregistered");
		event.getHeaders().put(AbstractAgent.PROPERTY_SLC_AGENT_ID,
				agentDescriptor.getUuid());
		publishEvent(event);
	}

	public void setEventPublishers(List<SlcEventPublisher> eventPublishers) {
		this.eventPublishers = eventPublishers;
	}

	protected void publishEvent(SlcEvent event) {

		for (Iterator<SlcEventPublisher> it = eventPublishers.iterator(); it
				.hasNext();) {
			SlcEventPublisher eventPublisher = it.next();
			if (log.isTraceEnabled())
				log.debug("Publish event: "
						+ event.getHeaders().get(SlcEvent.EVENT_TYPE) + " to "
						+ eventPublisher);
			eventPublisher.publish(event);
		}
	}
}
