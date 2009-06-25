package org.argeo.slc.jms;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.UUID;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.core.runtime.AbstractAgent;
import org.argeo.slc.execution.ExecutionModuleDescriptor;
import org.argeo.slc.msg.ExecutionAnswer;
import org.argeo.slc.msg.MsgConstants;
import org.argeo.slc.msg.ReferenceList;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.runtime.SlcAgent;
import org.argeo.slc.runtime.SlcAgentDescriptor;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;

/** JMS based implementation of SLC Agent. */
public class JmsAgent extends AbstractAgent implements SlcAgent,
		InitializingBean, DisposableBean, MessageListener {
	public final static String PROPERTY_QUERY = "query";
	public final static String QUERY_PING_ALL = "pingAll";

	private final static Log log = LogFactory.getLog(JmsAgent.class);

	private final SlcAgentDescriptor agentDescriptor;
	private JmsTemplate jmsTemplate;
	private Destination agentRegister;
	private Destination agentUnregister;

	private Destination responseDestination;

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
		try {
			jmsTemplate.convertAndSend(agentRegister, agentDescriptor);
			log.info("Agent #" + agentDescriptor.getUuid() + " registered to "
					+ agentRegister);
		} catch (JmsException e) {
			log
					.warn("Could not register agent "
							+ agentDescriptor.getUuid()
							+ " to server: "
							+ e.getMessage()
							+ ". The agent will stay offline but will keep listening for a ping all sent by server.");
			if (log.isTraceEnabled())
				log.debug("Original error.", e);
		}
	}

	public void destroy() throws Exception {
		try {
			jmsTemplate.convertAndSend(agentUnregister, agentDescriptor);
			log.info("Agent #" + agentDescriptor.getUuid()
					+ " unregistered from " + agentUnregister);
		} catch (JmsException e) {
			log.warn("Could not unregister agent " + agentDescriptor.getUuid()
					+ ": " + e.getMessage());
			if (log.isTraceEnabled())
				log.debug("Original error.", e);
		}
	}

	public void setAgentRegister(Destination agentRegister) {
		this.agentRegister = agentRegister;
	}

	public void setAgentUnregister(Destination agentUnregister) {
		this.agentUnregister = agentUnregister;
	}

	public String getMessageSelector() {
		String messageSelector = "slc_agentId='" + agentDescriptor.getUuid()
				+ "'";
		// if (log.isDebugEnabled())
		// log.debug("Message selector: " + messageSelector);
		return messageSelector;
	}

	public ExecutionModuleDescriptor getExecutionModuleDescriptor(
			String moduleName, String version) {
		return getModulesManager().getExecutionModuleDescriptor(moduleName,
				version);
	}

	public List<ExecutionModuleDescriptor> listExecutionModuleDescriptors() {
		return getModulesManager().listExecutionModules();
	}

	public boolean ping() {
		return true;
	}

	public void onMessage(final Message message) {
		final String query;
		final String correlationId;
		try {
			query = message.getStringProperty(PROPERTY_QUERY);
			correlationId = message.getJMSCorrelationID();
		} catch (JMSException e1) {
			throw new SlcException("Cannot analyze incoming message " + message);
		}

		final Object response;
		final Destination destinationSend;
		if (QUERY_PING_ALL.equals(query)) {
			ReferenceList refList = (ReferenceList) convertFrom(message);
			if (!refList.getReferences().contains(agentDescriptor.getUuid())) {
				response = agentDescriptor;
				destinationSend = agentRegister;
				log.info("Agent #" + agentDescriptor.getUuid()
						+ " registering to " + agentRegister
						+ " in reply to a " + QUERY_PING_ALL + " query");
			} else {
				return;
			}
		} else {
			response = process(query, message);
			destinationSend = responseDestination;
		}

		// Send response
		jmsTemplate.convertAndSend(destinationSend, response,
				new MessagePostProcessor() {
					public Message postProcessMessage(Message messageToSend)
							throws JMSException {
						messageToSend.setStringProperty(PROPERTY_QUERY, query);
						messageToSend.setStringProperty(
								MsgConstants.PROPERTY_SLC_AGENT_ID,
								agentDescriptor.getUuid());
						messageToSend.setJMSCorrelationID(correlationId);
						return messageToSend;
					}
				});
		if (log.isTraceEnabled())
			log.debug("Sent response to query '" + query
					+ "' with correlationId " + correlationId);
	}

	/** @return response */
	public Object process(String query, Message message) {
		try {
			if ("getExecutionModuleDescriptor".equals(query)) {
				String moduleName = message.getStringProperty("moduleName");
				String version = message.getStringProperty("version");
				return getExecutionModuleDescriptor(moduleName, version);
			} else if ("listExecutionModuleDescriptors".equals(query)) {

				List<ExecutionModuleDescriptor> lst = listExecutionModuleDescriptors();
				SlcAgentDescriptor agentDescriptorToSend = new SlcAgentDescriptor(
						agentDescriptor);
				agentDescriptorToSend.setModuleDescriptors(lst);
				return agentDescriptorToSend;
			} else if ("runSlcExecution".equals(query)) {
				final SlcExecution slcExecution = (SlcExecution) convertFrom(message);
				new Thread() {
					public void run() {
						runSlcExecution(slcExecution);
					}
				}.start();
				return ExecutionAnswer.ok("Execution started on agent "
						+ agentDescriptor.getUuid());
			} else if ("ping".equals(query)) {
				return ExecutionAnswer.ok("Agent " + agentDescriptor.getUuid()
						+ " is alive.");
			} else {
				throw new SlcException("Unsupported query " + query);
			}
		} catch (Exception e) {
			log.error("Processing of query " + query + " failed", e);
			return ExecutionAnswer.error(e);
		}
	}

	protected Object convertFrom(Message message) {
		try {
			return jmsTemplate.getMessageConverter().fromMessage(message);
		} catch (JMSException e) {
			throw new SlcException("Cannot convert message", e);
		}
	}

	public void setResponseDestination(Destination responseDestination) {
		this.responseDestination = responseDestination;
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

}
