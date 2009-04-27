package org.argeo.slc.jms;

import java.util.List;
import java.util.UUID;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.execution.ExecutionModuleDescriptor;
import org.argeo.slc.runtime.SlcAgent;
import org.argeo.slc.runtime.SlcAgentDescriptor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.support.converter.MessageConverter;

public class JmsAgentProxy implements SlcAgent {
	private final static Log log = LogFactory.getLog(JmsAgentProxy.class);

	private final String agentUuid;
	private final Destination requestDestination;
	private final Destination responseDestination;
	private final JmsTemplate jmsTemplate;
	private final MessageConverter messageConverter;

	public JmsAgentProxy(String agentUuid, Destination requestDestination,
			Destination responseDestination, JmsTemplate jmsTemplate,
			MessageConverter messageConverter) {
		this.agentUuid = agentUuid;
		this.requestDestination = requestDestination;
		this.responseDestination = responseDestination;
		this.jmsTemplate = jmsTemplate;
		this.messageConverter = messageConverter;
	}

	public ExecutionModuleDescriptor getExecutionModuleDescriptor(
			final String moduleName, final String version) {
		return (ExecutionModuleDescriptor) sendReceive(new AgentProxyMessageCreator(
				"getExecutionModuleDescriptor") {
			public void setArguments(Message message) throws JMSException {
				message.setStringProperty("moduleName", moduleName);
				message.setStringProperty("version", version);
			}
		});
	}

	public List<ExecutionModuleDescriptor> listExecutionModuleDescriptors() {
		return ((SlcAgentDescriptor) sendReceive(new AgentProxyMessageCreator(
				"listExecutionModuleDescriptors"))).getModuleDescriptors();
	}

	protected Object sendReceive(AgentProxyMessageCreator messageCreator) {
		String correlationId = UUID.randomUUID().toString();
		messageCreator.setCorrelationId(correlationId);
		send(messageCreator);
		return processResponse(correlationId);
	}

	protected void send(AgentProxyMessageCreator messageCreator) {
		jmsTemplate.send(requestDestination, messageCreator);
		if (log.isDebugEnabled())
			log.debug("Sent request" + messageCreator.getQuery() + " to agent "
					+ agentUuid + " with correlationId "
					+ messageCreator.getCorrelationId());
	}

	protected Object processResponse(String correlationId) {
		try {
			Message responseMsg = jmsTemplate.receiveSelected(
					responseDestination, "JMSCorrelationID='" + correlationId
							+ "'");
			if (log.isDebugEnabled())
				log.debug("Received response with correlationId "
						+ correlationId);
			return messageConverter.fromMessage(responseMsg);
		} catch (Exception e) {
			throw new SlcException("Could not process response from agent "
					+ agentUuid + " with correlationId " + correlationId, e);
		}
	}

	protected class AgentProxyMessageCreator implements MessageCreator {
		private final String query;
		private String correlationId;

		public AgentProxyMessageCreator(String query) {
			this.query = query;
		}

		public final Message createMessage(Session session) throws JMSException {
			if (agentUuid == null)
				throw new SlcException("Agent UUID not set");
			if (correlationId == null)
				throw new SlcException("JMSCorrelationID not set");
			TextMessage msg = session.createTextMessage();
			msg.setStringProperty(JmsAgent.PROPERTY_SLC_AGENT_ID, agentUuid);
			msg.setStringProperty(JmsAgent.PROPERTY_QUERY, query);
			msg.setJMSCorrelationID(correlationId);
			setArguments(msg);
			return msg;
		}

		protected void setArguments(Message message) throws JMSException {
		}

		public String getQuery() {
			return query;
		}

		public String getCorrelationId() {
			return correlationId;
		}

		public void setCorrelationId(String correlationId) {
			this.correlationId = correlationId;
		}

	}
}
