package org.argeo.slc.jms;

import java.util.Map;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

import org.argeo.slc.msg.event.SlcEvent;
import org.argeo.slc.msg.event.SlcEventPublisher;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;

public class JmsSlcEventPublisher implements SlcEventPublisher {
	private Destination eventsDestination;
	private JmsTemplate jmsTemplate;

	public void publish(final SlcEvent event) {
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
		// jmsTemplate.send(eventsDestination, new MessageCreator() {
		// public Message createMessage(Session session) throws JMSException {
		// TextMessage msg = session.createTextMessage();
		// // TODO: remove workaround when upgrading to ActiveMQ 5.3
		// // Workaround for
		// // https://issues.apache.org/activemq/browse/AMQ-2046
		// msg.setText("");
		//
		// return msg;
		// }
		// });
	}

	public void setEventsDestination(Destination eventsDestination) {
		this.eventsDestination = eventsDestination;
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

}
