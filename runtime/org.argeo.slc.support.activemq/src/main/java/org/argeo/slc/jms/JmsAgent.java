package org.argeo.slc.jms;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.core.runtime.AbstractAgent;
import org.argeo.slc.runtime.SlcAgent;
import org.argeo.slc.runtime.SlcAgentDescriptor;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;

/** JMS based implementation of SLC Agent. */
public class JmsAgent extends AbstractAgent implements SlcAgent,
		InitializingBean, DisposableBean {
	private final static Log log = LogFactory.getLog(JmsAgent.class);

	private final SlcAgentDescriptor agentDescriptor;
	private ConnectionFactory connectionFactory;
	private JmsTemplate jmsTemplate;
	private Destination agentRegister;
	private Destination agentUnregister;

	private MessageConverter messageConverter;

	public JmsAgent() {
		try {
			agentDescriptor = new SlcAgentDescriptor();
			agentDescriptor.setUuid(UUID.randomUUID().toString());
			agentDescriptor.setHost(InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			throw new SlcException("Unable to create agent descriptor.", e);
		}
	}

	public void afterPropertiesSet() throws Exception {
		// Initialize JMS Template
		jmsTemplate = new JmsTemplate(connectionFactory);
		jmsTemplate.setMessageConverter(messageConverter);

		jmsTemplate.convertAndSend(agentRegister, agentDescriptor);
		log.info("Agent #" + agentDescriptor.getUuid() + " registered to "
				+ agentRegister);
	}

	public void destroy() throws Exception {
		jmsTemplate.convertAndSend(agentUnregister, agentDescriptor);
		log.info("Agent #" + agentDescriptor.getUuid() + " unregistered from "
				+ agentUnregister);
	}

	public void setAgentRegister(Destination agentRegister) {
		this.agentRegister = agentRegister;
	}

	public void setAgentUnregister(Destination agentUnregister) {
		this.agentUnregister = agentUnregister;
	}

	/*
	 * public void onMessage(Message message) { // FIXME: we filter the messages
	 * on the client side, // because of a weird problem with selector since
	 * moving to OSGi try { if (message.getStringProperty("slc-agentId").equals(
	 * agentDescriptor.getUuid())) { runSlcExecution((SlcExecution)
	 * messageConverter .fromMessage(message)); } else { if
	 * (log.isDebugEnabled()) log.debug("Filtered out message " + message); } }
	 * catch (JMSException e) { throw new SlcException("Cannot convert message "
	 * + message, e); }
	 * 
	 * }
	 */

	public String getMessageSelector() {
		String messageSelector = "slc_agentId='" + agentDescriptor.getUuid()
				+ "'";
		// String messageSelector = "slc-agentId LIKE '%'";
		if (log.isDebugEnabled())
			log.debug("Message selector: " + messageSelector);
		return messageSelector;
	}

	public void setMessageConverter(MessageConverter messageConverter) {
		this.messageConverter = messageConverter;
	}

	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

}
