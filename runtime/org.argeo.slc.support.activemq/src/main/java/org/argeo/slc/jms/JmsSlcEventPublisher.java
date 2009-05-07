package org.argeo.slc.jms;

import java.util.Map;

import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

import org.argeo.slc.SlcException;
import org.argeo.slc.msg.event.SlcEvent;
import org.argeo.slc.msg.event.SlcEventPublisher;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;

public class JmsSlcEventPublisher implements SlcEventPublisher {
	private Destination eventsDestination;
	private JmsTemplate jmsTemplate;

	public void publish(final SlcEvent event) {
		if (jmsTemplate.getDeliveryMode() != DeliveryMode.PERSISTENT)
			throw new SlcException(
					"Delivery mode has to be persistent in order to have durable subscription");

		jmsTemplate.convertAndSend(eventsDestination, event,
				new MessagePostProcessor() {

					public Message postProcessMessage(Message message)
							throws JMSException {
						Map<String, String> headers = event.getHeaders();
						for (String key : headers.keySet()) {
							message.setStringProperty(key, headers.get(key));
						}
						return message;
					}
				});
	}

	public void setEventsDestination(Destination eventsDestination) {
		this.eventsDestination = eventsDestination;
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

}
