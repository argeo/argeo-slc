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
import org.argeo.slc.core.runtime.AbstractAgent;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.runtime.SlcAgent;
import org.argeo.slc.runtime.SlcAgentDescriptor;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jms.core.JmsTemplate;

/** JMS based implementation of SLC Agent. */
public class JmsAgent extends AbstractAgent implements SlcAgent, InitializingBean, DisposableBean {
	private final static Log log = LogFactory.getLog(JmsAgent.class);

	private final SlcAgentDescriptor agentDescriptor;
	private JmsTemplate jmsTemplate;
	private Destination agentRegister;
	private Destination agentUnregister;

	private String agentDestinationPrefix = "agent.";
	private String agentDestinationBase;

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
		agentDestinationBase = agentDestinationPrefix
				+ agentDescriptor.getUuid() + ".";
		jmsTemplate.convertAndSend(agentRegister, agentDescriptor);
		log.info("Agent #" + agentDescriptor.getUuid() + " registered to "
				+ agentRegister);
	}

	public void destroy() throws Exception {
		jmsTemplate.convertAndSend(agentUnregister, agentDescriptor);
		log.info("Agent #" + agentDescriptor.getUuid() + " unregistered to "
				+ agentRegister);
	}

	public String actionDestinationName(String action) {
		return agentDestinationBase + action;
	}

	public void newExecution(SlcExecution slcExecution) {
		log.info("Execute SlcExecution :" + slcExecution);
		runSlcExecution(slcExecution);
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	public void setAgentRegister(Destination agentRegister) {
		this.agentRegister = agentRegister;
	}

	public void setAgentUnregister(Destination agentUnregister) {
		this.agentUnregister = agentUnregister;
	}

	public void setAgentDestinationPrefix(String agentDestinationPrefix) {
		this.agentDestinationPrefix = agentDestinationPrefix;
	}

}
