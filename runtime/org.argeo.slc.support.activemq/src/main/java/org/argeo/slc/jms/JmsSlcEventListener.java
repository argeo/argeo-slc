package org.argeo.slc.jms;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.msg.event.SlcEvent;
import org.argeo.slc.msg.event.SlcEventListener;
import org.argeo.slc.msg.event.SlcEventListenerDescriptor;
import org.springframework.jms.connection.ConnectionFactoryUtils;
import org.springframework.jms.support.JmsUtils;
import org.springframework.jms.support.converter.MessageConverter;

public class JmsSlcEventListener implements SlcEventListener {
	private final static Log log = LogFactory.getLog(JmsSlcEventListener.class);

	private Topic eventsDestination;
	private ConnectionFactory jmsConnectionFactory;
	private MessageConverter messageConverter;

	private Connection connection = null;
	private String connectionClientId = getClass() + "#"
			+ UUID.randomUUID().toString();

	private List<String> subscriberIds = new ArrayList<String>();

	private Boolean isClosed = false;

	// private Map<String, ListeningClient> clients = Collections
	// .synchronizedMap(new HashMap<String, ListeningClient>());

	public SlcEvent listen(String subscriberId,
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
			synchronized (subscriberIds) {
				while (subscriberIds.contains(subscriberId)) {
					try {
						subscriberIds.wait(500);
						if (isClosed)
							return null;
					} catch (InterruptedException e) {
						// silent
					}
				}

				subscriberIds.add(subscriberId);
				Session session = null;
				TopicSubscriber topicSubscriber = null;
				try {
					// ListeningClient client = (ListeningClient)
					// getClient(clientId);
					session = connection.createSession(false,
							Session.AUTO_ACKNOWLEDGE);
					topicSubscriber = session.createDurableSubscriber(
							eventsDestination, subscriberId,
							createSelector(descriptors), true);
					Message message = topicSubscriber.receive(timeout);
					obj = messageConverter.fromMessage(message);
				} catch (JMSException e) {
					throw new SlcException("Cannot poll events for subscriber "
							+ subscriberId, e);
				} finally {
					JmsUtils.closeMessageConsumer(topicSubscriber);
					JmsUtils.closeSession(session);
					subscriberIds.remove(subscriberId);
					subscriberIds.notifyAll();
				}

			}

			if (obj == null)
				return null;
			else
				return (SlcEvent) obj;
		}
	}

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

	public void init() {
		try {
			connection = jmsConnectionFactory.createConnection();
			connection.setClientID(connectionClientId);
			connection.start();
		} catch (JMSException e) {
			throw new SlcException("Could not init connection", e);
		}
	}

	public void close() {
		ConnectionFactoryUtils.releaseConnection(connection,
				jmsConnectionFactory, true);
		isClosed = true;
		synchronized (subscriberIds) {
			subscriberIds.notifyAll();
		}
	}

	public boolean isClosed() {
		return isClosed;
	}

	// public void close(String clientId) {
	// // Session session = null;
	// // // ListeningClient client = getClient(clientId);
	// // // Connection connection = client.getConnection();
	// // try {
	// // session = client.getSession();
	// // session.unsubscribe(clientId);
	// // } catch (JMSException e) {
	// // log.warn("Could not unsubscribe client " + clientId, e);
	// // } finally {
	// // JmsUtils.closeSession(session);
	// // }
	// //
	// // // synchronized (client) {
	// // // clients.remove(clientId);
	// // // client.notify();
	// // // }
	// }

	// protected ListeningClient getClient(String clientId) {
	// ListeningClient client = clients.get(clientId);
	// if (client == null) {
	// // Lazy init
	// client = new ListeningClient(connection);
	// clients.put(clientId, client);
	// }
	// return client;
	// }

	// protected class ListeningClient {
	// private final Connection connection;
	// private final Session session;
	//
	// public ListeningClient(Connection connection) {
	// super();
	// this.connection = connection;
	// try {
	// session = connection.createSession(false,
	// Session.AUTO_ACKNOWLEDGE);
	// } catch (JMSException e) {
	// throw new SlcException("Cannot create session");
	// }
	// }
	//
	// public Connection getConnection() {
	// return connection;
	// }
	//
	// public Session getSession() {
	// return session;
	// }
	//
	// }
}
