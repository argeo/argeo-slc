package org.argeo.slc.jms;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.msg.event.SlcEvent;
import org.argeo.slc.msg.event.SlcEventListener;
import org.argeo.slc.msg.event.SlcEventListenerDescriptor;
import org.argeo.slc.msg.event.SlcEventListenerRegister;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.JmsUtils;
import org.springframework.jms.support.converter.MessageConverter;

public class JmsSlcEventListener implements SlcEventListener {
	private final static Log log = LogFactory.getLog(JmsSlcEventListener.class);

	private Topic eventsDestination;
	private ConnectionFactory jmsConnectionFactory;
	private MessageConverter messageConverter;

	private Map<String, ListeningClient> clients = Collections
			.synchronizedMap(new HashMap<String, ListeningClient>());

	public SlcEvent listen(String clientId,
			List<SlcEventListenerDescriptor> descriptors, Long timeout) {
		if (descriptors.size() == 0) {
			// No listeners, just waiting
			try {
				Thread.sleep(timeout);
			} catch (InterruptedException e) {
				// silent
			}
			return null;
		} else {
			String selector = createSelector(descriptors);

			if (log.isTraceEnabled())
				log.debug("Selector: " + selector);

			Object obj = null;
			Session session = null;
			TopicSubscriber topicSubscriber = null;
			// MessageConsumer messageConsumer = null;
			try {
				// Connection connection = getClient(clientId).getConnection();
				// session = connection.createSession(false,
				// Session.AUTO_ACKNOWLEDGE);
				session = getClient(clientId).getSession();
				topicSubscriber = session.createDurableSubscriber(
						eventsDestination, clientId,
						createSelector(descriptors), true);
				Message message = topicSubscriber.receive(timeout);
				// messageConsumer = session.createConsumer(eventsDestination,
				// createSelector(descriptors));
				// Message message = messageConsumer.receive(timeout);
				obj = messageConverter.fromMessage(message);
			} catch (JMSException e) {
				throw new SlcException("Cannot poll events for client "
						+ clientId, e);
			} finally {
				// JmsUtils.closeMessageConsumer(messageConsumer);
				JmsUtils.closeMessageConsumer(topicSubscriber);
				// JmsUtils.closeSession(session);
			}

			if (obj == null)
				return null;
			else
				return (SlcEvent) obj;
		}
	}

	/*
	 * public SlcEvent listen(SlcEventListenerRegister register, Long timeout) {
	 * 
	 * JmsTemplate jmsTemplate = new JmsTemplate(jmsConnectionFactory);
	 * jmsTemplate.setMessageConverter(messageConverter);
	 * jmsTemplate.setReceiveTimeout(timeout);
	 * 
	 * List<SlcEventListenerDescriptor> descriptors = register
	 * .getDescriptorsCopy();
	 * 
	 * if (descriptors.size() == 0) { // No listeners, just waiting try {
	 * Thread.sleep(timeout); } catch (InterruptedException e) { // silent }
	 * return null; } else { String selector = createSelector(descriptors);
	 * 
	 * if (log.isTraceEnabled()) log.debug("Selector: " + selector);
	 * 
	 * Object obj = jmsTemplate.receiveSelectedAndConvert( eventsDestination,
	 * selector);
	 * 
	 * if (obj == null) return null; else return (SlcEvent) obj; } }
	 */

	/** Returns null if no filter */
	protected String createSelector(List<SlcEventListenerDescriptor> descriptors) {
		if (descriptors.size() == 0)
			throw new SlcException("No listeners, cannot generate JMS selector");

		StringBuffer buf = new StringBuffer(256);
		Boolean first = true;
		for (SlcEventListenerDescriptor descriptor : descriptors) {
			if (first)
				first = false;
			else
				buf.append(" OR ");

			buf.append('(');
			buf.append(SlcEvent.EVENT_TYPE).append("=").append('\'').append(
					descriptor.getEventType()).append('\'');
			if (descriptor.getFilter() != null) {
				buf.append(" AND ");
				buf.append('(').append(descriptor.getFilter()).append(')');
			}
			buf.append(')');
		}
		return buf.toString();
	}

	public void setEventsDestination(Topic eventsDestination) {
		this.eventsDestination = eventsDestination;
	}

	public void setJmsConnectionFactory(ConnectionFactory jmsConnectionFactory) {
		this.jmsConnectionFactory = jmsConnectionFactory;
	}

	public void setMessageConverter(MessageConverter messageConverter) {
		this.messageConverter = messageConverter;
	}

	public ListeningClient init(String clientId) {
		Connection connection = null;
		try {
			connection = jmsConnectionFactory.createConnection();
			connection.setClientID(clientId);
			connection.start();
			ListeningClient client = new ListeningClient(connection);
			return client;
		} catch (JMSException e) {
			throw new SlcException("Could not init listening client "
					+ clientId, e);
		} finally {
		}
	}

	public void close(String clientId) {
		Session session = null;
		ListeningClient client = getClient(clientId);
		Connection connection = client.getConnection();
		try {
			session = client.getSession();
			session.unsubscribe(clientId);
		} catch (JMSException e) {
			log.warn("Could not unsubscribe client " + clientId, e);
		} finally {
			JmsUtils.closeSession(session);
		}

		// JmsUtils.closeSession(client.getSession());
		clients.remove(clientId);

		try {
			connection.stop();
			connection.close();
		} catch (JMSException e) {
			throw new SlcException("Could not close JMS connection for client "
					+ clientId, e);
		} finally {
			clients.remove(clientId);
		}
	}

	protected ListeningClient getClient(String clientId) {
		ListeningClient client = clients.get(clientId);
		if (client == null) {
			// Lazy init
			client = init(clientId);
			clients.put(clientId, client);
		}
		return client;
	}

	protected class ListeningClient {
		private final Connection connection;
		private final Session session;

		public ListeningClient(Connection connection) {
			super();
			this.connection = connection;
			try {
				session = connection.createSession(false,
						Session.AUTO_ACKNOWLEDGE);
			} catch (JMSException e) {
				throw new SlcException("Cannot create session");
			}
		}

		public Connection getConnection() {
			return connection;
		}

		public Session getSession() {
			return session;
		}

	}
}
