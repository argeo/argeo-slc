package org.argeo.slc.jms;

import java.util.UUID;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.jms.listener.SessionAwareMessageListener;

/** Temporary hack*/
public class JmsTransferNewExecution implements SessionAwareMessageListener {
	private Destination requestDestination;

	public void onMessage(Message message, Session session) throws JMSException {
		TextMessage messageToSend = session
				.createTextMessage(((TextMessage) message).getText());
		messageToSend
				.setStringProperty(JmsAgent.PROPERTY_QUERY, "newExecution");
		messageToSend.setStringProperty(JmsAgent.PROPERTY_SLC_AGENT_ID, message
				.getStringProperty(JmsAgent.PROPERTY_SLC_AGENT_ID));
		messageToSend.setJMSCorrelationID(UUID.randomUUID().toString());
		MessageProducer producer = session.createProducer(requestDestination);
		producer.send(messageToSend);
	}

	public void setRequestDestination(Destination requestDestination) {
		this.requestDestination = requestDestination;
	}

}
