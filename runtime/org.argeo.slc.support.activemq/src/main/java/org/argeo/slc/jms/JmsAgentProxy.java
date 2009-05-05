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
import org.argeo.slc.msg.ExecutionAnswer;
import org.argeo.slc.msg.MsgConstants;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.runtime.SlcAgent;
import org.argeo.slc.runtime.SlcAgentDescriptor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class JmsAgentProxy implements SlcAgent {
	private final static Log log = LogFactory.getLog(JmsAgentProxy.class);

	private final String agentUuid;
	private final Destination requestDestination;
	private final Destination responseDestination;
	private final JmsTemplate jmsTemplate;

	public JmsAgentProxy(String agentUuid, Destination requestDestination,
			Destination responseDestination, JmsTemplate jmsTemplate) {
		this.agentUuid = agentUuid;
		this.requestDestination = requestDestination;
		this.responseDestination = responseDestination;
		this.jmsTemplate = jmsTemplate;
	}

	public ExecutionModuleDescriptor getExecutionModuleDescriptor(
			final String moduleName, final String version) {
		return (ExecutionModuleDescriptor) sendReceive(new AgentMC(
				"getExecutionModuleDescriptor") {
			public void setArguments(Message message) throws JMSException {
				message.setStringProperty("moduleName", moduleName);
				message.setStringProperty("version", version);
			}
		});
	}

	public List<ExecutionModuleDescriptor> listExecutionModuleDescriptors() {
		return ((SlcAgentDescriptor) sendReceive(new AgentMC(
				"listExecutionModuleDescriptors"))).getModuleDescriptors();
	}

	public void runSlcExecution(SlcExecution slcExecution) {
		sendReceive(new AgentMC("runSlcExecution", slcExecution));
	}

	public boolean ping() {
		Object response = sendReceive(new AgentMC("ping"), false);
		if (response == null)
			return false;
		else {
			ExecutionAnswer answer = (ExecutionAnswer) response;
			return ExecutionAnswer.OK.equals(answer.getStatus());
		}
	}

	protected Object sendReceive(AgentMC messageCreator) {
		return sendReceive(messageCreator, true);
	}

	/**
	 * @param timeoutException
	 *            if true throws an exception if reception timeouted, else
	 *            return null
	 */
	protected Object sendReceive(AgentMC messageCreator,
			boolean timeoutException) {
		String correlationId = UUID.randomUUID().toString();
		messageCreator.setCorrelationId(correlationId);
		send(messageCreator);

		Object response = processResponse(messageCreator, timeoutException);

		if (response instanceof ExecutionAnswer) {
			ExecutionAnswer answer = (ExecutionAnswer) response;
			if (ExecutionAnswer.ERROR.equals(answer.getStatus()))
				throw new SlcException("Execution of '"
						+ messageCreator.getQuery() + "' failed on the agent "
						+ agentUuid + ": " + answer.getMessage()
						+ " (correlationId=" + correlationId + ")");
			else
				return answer;
		} else {
			return response;
		}
	}

	protected void send(AgentMC messageCreator) {
		jmsTemplate.send(requestDestination, messageCreator);
		if (log.isTraceEnabled())
			log.debug("Sent query '" + messageCreator.getQuery()
					+ "' with correlationId "
					+ messageCreator.getCorrelationId() + " to agent "
					+ agentUuid);
	}

	protected Object processResponse(AgentMC messageCreator,
			boolean timeoutException) {
		String correlationId = messageCreator.getCorrelationId();
		String query = messageCreator.getQuery();
		Message responseMsg = null;
		try {
			responseMsg = jmsTemplate.receiveSelected(responseDestination,
					"JMSCorrelationID='" + correlationId + "'");
		} catch (Exception e) {
			throw new SlcException("Could not receive response from agent "
					+ agentUuid + " with correlationId " + correlationId
					+ " (query '" + query + "')", e);
		}

		if (responseMsg == null) {// timeout
			if (timeoutException)
				throw new SlcException("TIMEOUT: Query '" + query + "'"
						+ " with correlationId " + correlationId
						+ " sent to agent " + agentUuid + " timed out.");
			else
				return null;
		}
		if (log.isTraceEnabled())
			log.debug("Received response for query '" + query
					+ "' with correlationId " + correlationId + " from agent "
					+ agentUuid);

		try {
			return fromMessage(responseMsg);
		} catch (Exception e) {
			throw new SlcException("Could not convert response from agent "
					+ agentUuid + " with correlationId " + correlationId
					+ " (query '" + query + "')", e);
		}
	}

	protected Object fromMessage(Message message) throws JMSException {
		return jmsTemplate.getMessageConverter().fromMessage(message);
	}

	protected Message toMessage(Object obj, Session session)
			throws JMSException {
		return jmsTemplate.getMessageConverter().toMessage(obj, session);
	}

	protected class AgentMC implements MessageCreator {
		private final String query;
		private Object body = null;
		private String correlationId;

		public AgentMC(String query) {
			this.query = query;
		}

		public AgentMC(String query, Object body) {
			this.query = query;
			this.body = body;
		}

		public final Message createMessage(Session session) throws JMSException {
			if (agentUuid == null)
				throw new SlcException("Agent UUID not set");
			if (correlationId == null)
				throw new SlcException("JMSCorrelationID not set");
			final Message msg;
			if (body == null)
				msg = session.createTextMessage();
			else
				msg = toMessage(body, session);
			msg.setStringProperty(MsgConstants.PROPERTY_SLC_AGENT_ID, agentUuid);
			msg.setStringProperty(JmsAgent.PROPERTY_QUERY, query);
			msg.setJMSCorrelationID(correlationId);
			setArguments(msg);
			if (msg instanceof TextMessage) {
				TextMessage textMessage = (TextMessage) msg;
				if (textMessage.getText() == null) {
					// TODO: remove workaround when upgrading to ActiveMQ 5.3
					// Workaround for
					// https://issues.apache.org/activemq/browse/AMQ-2046
					textMessage.setText("");
				}
			}
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
