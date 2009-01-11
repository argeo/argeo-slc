package org.argeo.slc.jms;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.runtime.SlcAgent;
import org.argeo.slc.runtime.SlcAgentDescriptor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jms.core.JmsTemplate;

/** JMS based implementation of SLC Agent. */
public class JmsAgent implements SlcAgent, MessageListener, InitializingBean {
	private final static Log log = LogFactory.getLog(JmsAgent.class);

	private final SlcAgentDescriptor agentDescriptor;
	private JmsTemplate jmsTemplate;
	private Destination agentRegister;

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
		jmsTemplate.convertAndSend(agentRegister, agentDescriptor);
		log.info("Agent #" + agentDescriptor.getUuid() + " registered to "
				+ agentRegister);
	}

	public void onMessage(Message message) {
		try {
			log.info("Received message " + message.getJMSMessageID());
		} catch (JMSException e) {
			e.printStackTrace();
		}

	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	public void setAgentRegister(Destination agentRegister) {
		this.agentRegister = agentRegister;
	}

}
