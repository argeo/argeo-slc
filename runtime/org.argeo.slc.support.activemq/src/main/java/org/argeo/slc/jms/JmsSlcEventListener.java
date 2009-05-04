package org.argeo.slc.jms;

import java.util.List;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.msg.event.SlcEvent;
import org.argeo.slc.msg.event.SlcEventListener;
import org.argeo.slc.msg.event.SlcEventListenerDescriptor;
import org.argeo.slc.msg.event.SlcEventListenerRegister;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;

public class JmsSlcEventListener implements SlcEventListener {
	private final static Log log = LogFactory.getLog(JmsSlcEventListener.class);

	private Destination eventsDestination;
	private ConnectionFactory jmsConnectionFactory;
	private MessageConverter messageConverter;

	public SlcEvent listen(SlcEventListenerRegister register, Long timeout) {
		JmsTemplate jmsTemplate = new JmsTemplate(jmsConnectionFactory);
		jmsTemplate.setMessageConverter(messageConverter);
		jmsTemplate.setReceiveTimeout(timeout);

		List<SlcEventListenerDescriptor> descriptors = register
				.getDescriptorsCopy();

		if (descriptors.size() == 0) {
			// No listeners, just waiting
			try {
				Thread.sleep(timeout);
			} catch (InterruptedException e) {
				// silent
			}
			return null;
		} else {
			String selector = createSelector(descriptors);

			if (log.isTraceEnabled())
				log.debug("Selector: " + selector);

			Object obj = jmsTemplate.receiveSelectedAndConvert(
					eventsDestination, selector);

			if (obj == null)
				return null;
			else
				return (SlcEvent) obj;
		}
	}

	/** Returns null if no filter */
	protected String createSelector(List<SlcEventListenerDescriptor> descriptors) {
		if (descriptors.size() == 0)
			throw new SlcException("No listeners, cannot generate JMS selector");

		StringBuffer buf = new StringBuffer(256);
		Boolean first = true;
		for (SlcEventListenerDescriptor descriptor : descriptors) {
			if (first)
				first = false;
			else
				buf.append(" OR ");

			buf.append('(');
			buf.append(SlcEvent.EVENT_TYPE).append("=").append('\'').append(
					descriptor.getEventType()).append('\'');
			if (descriptor.getFilter() != null) {
				buf.append(" AND ");
				buf.append('(').append(descriptor.getFilter()).append(')');
			}
			buf.append(')');
		}
		return buf.toString();
	}

	public void setEventsDestination(Destination eventsDestination) {
		this.eventsDestination = eventsDestination;
	}

	public void setJmsConnectionFactory(ConnectionFactory jmsConnectionFactory) {
		this.jmsConnectionFactory = jmsConnectionFactory;
	}

	public void setMessageConverter(MessageConverter messageConverter) {
		this.messageConverter = messageConverter;
	}

}
