package org.argeo.slc.jms;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.core.runtime.AbstractAgent;
import org.argeo.slc.execution.ExecutionModule;
import org.argeo.slc.execution.ExecutionModuleDescriptor;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.runtime.SlcAgent;
import org.argeo.slc.runtime.SlcAgentDescriptor;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.jms.support.converter.MessageConversionException;

/** JMS based implementation of SLC Agent. */
public class JmsAgent extends AbstractAgent implements SlcAgent,
		InitializingBean, DisposableBean, SessionAwareMessageListener {
	public final static String PROPERTY_QUERY = "query";
	public final static String PROPERTY_SLC_AGENT_ID = "slc_agentId";

	private final static Log log = LogFactory.getLog(JmsAgent.class);

	private final SlcAgentDescriptor agentDescriptor;
	// private ConnectionFactory connectionFactory;
	private JmsTemplate jmsTemplate;
	private Destination agentRegister;
	private Destination agentUnregister;

	// private Destination requestDestination;
	private Destination responseDestination;

	// private MessageConverter messageConverter;

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
		// jmsTemplate = new JmsTemplate(connectionFactory);
		// jmsTemplate.setMessageConverter(messageConverter);

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
		// if (log.isDebugEnabled())
		// log.debug("Message selector: " + messageSelector);
		return messageSelector;
	}

	public void onMessage(Message message, Session session) throws JMSException {
		MessageProducer producer = session.createProducer(responseDestination);
		String query = message.getStringProperty(PROPERTY_QUERY);
		String correlationId = message.getJMSCorrelationID();
		if (log.isDebugEnabled())
			log.debug("Received query " + query + " with correlationId "
					+ correlationId);

		Message responseMsg = null;
		if ("getExecutionModuleDescriptor".equals(query)) {
			String moduleName = message.getStringProperty("moduleName");
			String version = message.getStringProperty("version");
			ExecutionModuleDescriptor emd = getExecutionModuleDescriptor(
					moduleName, version);
			responseMsg = jmsTemplate.getMessageConverter().toMessage(emd,
					session);
		} else if ("listExecutionModuleDescriptors".equals(query)) {

			List<ExecutionModuleDescriptor> lst = listExecutionModuleDescriptors();
			SlcAgentDescriptor agentDescriptorToSend = new SlcAgentDescriptor(
					agentDescriptor);
			agentDescriptorToSend.setModuleDescriptors(lst);
			responseMsg = jmsTemplate.getMessageConverter().toMessage(
					agentDescriptorToSend, session);
		} else if ("newExecution".equals(query)) {

			SlcExecution slcExecution = (SlcExecution) jmsTemplate
					.getMessageConverter().fromMessage(message);
			runSlcExecution(slcExecution);
		} else {
			// try {
			// // FIXME: generalize
			// SlcExecution slcExecution = (SlcExecution) jmsTemplate
			// .getMessageConverter().fromMessage(message);
			// runSlcExecution(slcExecution);
			// } catch (MessageConversionException e) {
			// if (log.isDebugEnabled())
			// log.debug("Unsupported query " + query, e);
			// }
			if (log.isDebugEnabled())
				log.debug("Unsupported query " + query);
			return;
		}

		if (responseMsg != null) {
			responseMsg.setJMSCorrelationID(correlationId);
			producer.send(responseMsg);
			if (log.isDebugEnabled())
				log.debug("Sent response to query " + query
						+ " with correlationId " + correlationId + ": "
						+ responseMsg);
		}

	}

	public ExecutionModuleDescriptor getExecutionModuleDescriptor(
			String moduleName, String version) {
		return getModulesManager().getExecutionModuleDescriptor(moduleName,
				version);
	}

	public List<ExecutionModuleDescriptor> listExecutionModuleDescriptors() {
		List<ExecutionModule> modules = getModulesManager()
				.listExecutionModules();

		List<ExecutionModuleDescriptor> descriptors = new ArrayList<ExecutionModuleDescriptor>();
		for (ExecutionModule module : modules) {
			ExecutionModuleDescriptor md = new ExecutionModuleDescriptor();
			md.setName(module.getName());
			md.setVersion(module.getVersion());
			descriptors.add(md);
		}
		return descriptors;
	}

	public void setResponseDestination(Destination responseDestination) {
		this.responseDestination = responseDestination;
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

}
