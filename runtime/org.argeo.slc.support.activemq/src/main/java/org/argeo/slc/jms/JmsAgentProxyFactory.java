package org.argeo.slc.jms;

import javax.jms.Destination;

import org.argeo.slc.runtime.SlcAgent;
import org.argeo.slc.runtime.SlcAgentFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;

public class JmsAgentProxyFactory implements SlcAgentFactory {
	private Destination requestDestination;
	private Destination responseDestination;
	private JmsTemplate jmsTemplate;
	private MessageConverter messageConverter;

	public SlcAgent getAgent(String uuid) {
		return new JmsAgentProxy(uuid, requestDestination, responseDestination,
				jmsTemplate, messageConverter);
	}

	public void setRequestDestination(Destination requestDestination) {
		this.requestDestination = requestDestination;
	}

	public void setResponseDestination(Destination responseDestination) {
		this.responseDestination = responseDestination;
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	public void setMessageConverter(MessageConverter messageConverter) {
		this.messageConverter = messageConverter;
	}

}
