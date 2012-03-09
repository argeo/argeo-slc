/*
 * Copyright (C) 2007-2012 Mathieu Baudier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

	// IoC
	private Topic eventsDestination;
	private ConnectionFactory jmsConnectionFactory;
	private MessageConverter messageConverter;

	// Initialized with init() method, released with close()
	private Connection connection = null;

	// One by instance
	private String connectionClientId = getClass() + "#"
			+ UUID.randomUUID().toString();
	private Boolean isClosed = false;

	private List<String> subscriberIds = new ArrayList<String>();

	// private Map<String, ListeningClient> clients = Collections
	// .synchronizedMap(new HashMap<String, ListeningClient>());

	public SlcEvent listen(String subscriberId,
			List<SlcEventListenerDescriptor> descriptors, Long timeout) {
		if (descriptors.size() == 0) {
			// No listener, just waiting
			try {
				if (log.isTraceEnabled())
					log.trace("No event listener registered, sleeping...");
				Thread.sleep(timeout);
			} catch (InterruptedException e) {
				// silent
			}
			return null;
		} else {
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
		if (log.isTraceEnabled())
			log.trace("selector created : " + buf.toString());
		return buf.toString();
	}

	public boolean isClosed() {
		return isClosed;
	}

	// Ioc
	public void setEventsDestination(Topic eventsDestination) {
		this.eventsDestination = eventsDestination;
	}

	public void setJmsConnectionFactory(ConnectionFactory jmsConnectionFactory) {
		this.jmsConnectionFactory = jmsConnectionFactory;
	}

	public void setMessageConverter(MessageConverter messageConverter) {
		this.messageConverter = messageConverter;
	}

	// Life Cycle
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
