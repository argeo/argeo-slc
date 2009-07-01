package org.argeo.slc.services;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.msg.MsgConstants;
import org.argeo.slc.msg.event.SlcEvent;
import org.argeo.slc.msg.event.SlcEventPublisher;
import org.argeo.slc.msg.process.SlcExecutionStatusRequest;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.runtime.SlcAgentDescriptor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class EventPublisherAspect {
	public final static String EVT_AGENT_REGISTERED = "agentRegistered";
	public final static String EVT_AGENT_UNREGISTERED = "agentUnregistered";
	public final static String EVT_NEW_SLC_EXECUTION = "newSlcExecution";
	public final static String EVT_UPDATE_SLC_EXECUTION_STATUS = "updateSlcExecutionStatus";

	private final static Log log = LogFactory
			.getLog(EventPublisherAspect.class);

	private List<SlcEventPublisher> eventPublishers;

	@After("execution(void org.argeo.slc.services.AgentService.register(..))")
	public void registerAgent(JoinPoint jp) throws Throwable {
		SlcAgentDescriptor agentDescriptor = (SlcAgentDescriptor) jp.getArgs()[0];
		SlcEvent event = new SlcEvent(EVT_AGENT_REGISTERED);
		event.getHeaders().put(MsgConstants.PROPERTY_SLC_AGENT_ID,
				agentDescriptor.getUuid());
		publishEvent(event);
	}

	@After("execution(void org.argeo.slc.services.AgentService.unregister(..))")
	public void unregisterAgent(JoinPoint jp) throws Throwable {
		SlcAgentDescriptor agentDescriptor = (SlcAgentDescriptor) jp.getArgs()[0];
		SlcEvent event = new SlcEvent(EVT_AGENT_UNREGISTERED);
		event.getHeaders().put(MsgConstants.PROPERTY_SLC_AGENT_ID,
				agentDescriptor.getUuid());
		publishEvent(event);
	}

	@After("execution(void org.argeo.slc.services.SlcExecutionService.newExecution(..))")
	public void newSlcExecution(JoinPoint jp) throws Throwable {
		SlcExecution slcExecution = (SlcExecution) jp.getArgs()[0];
		SlcEvent event = new SlcEvent(EVT_NEW_SLC_EXECUTION);
		event.getHeaders().put(MsgConstants.PROPERTY_SLC_EXECUTION_ID,
				slcExecution.getUuid());
		publishEvent(event);
	}

	@After("execution(void org.argeo.slc.services.SlcExecutionService.updateStatus(..))")
	public void updateSlcExecutionStatus(JoinPoint jp) throws Throwable {
		SlcExecutionStatusRequest msg = (SlcExecutionStatusRequest) jp
				.getArgs()[0];
		SlcEvent event = new SlcEvent(EVT_UPDATE_SLC_EXECUTION_STATUS);
		event.getHeaders().put(MsgConstants.PROPERTY_SLC_EXECUTION_ID,
				msg.getSlcExecutionUuid());
		event.getHeaders().put(MsgConstants.PROPERTY_SLC_EXECUTION_STATUS,
				msg.getNewStatus());
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
