package org.argeo.slc.demo.manager;

import java.net.URI;
import java.net.URISyntaxException;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class JmsTesting {
	private final static Log log = LogFactory.getLog(JmsTesting.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		BrokerService broker;
//		try {
//			broker = new BrokerService();
//			broker.setPersistent(false);
//			TransportConnector transportConnector = new TransportConnector();
//			transportConnector.setUri(new URI("tcp://localhost:61616"));
//			broker.addConnector(transportConnector);
//			broker.start();
//			
//			Thread.sleep(5000);
//		} catch (URISyntaxException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}

		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"org/argeo/slc/activemq/spring.xml");
		try {


			ConnectionFactory connectionFactory = (ConnectionFactory) applicationContext
					.getBean("slcDefault.jms.connectionFactory");
			JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);

			jmsTemplate.send("modulesManager.request", new MessageCreator() {

				public Message createMessage(Session session)
						throws JMSException {
					TextMessage message = session.createTextMessage();
					message.setStringProperty("action",
							"getExecutionModuleDescriptor");
					message.setStringProperty("name",
							"org.argeo.slc.demo.basic");
					message.setStringProperty("version", "LATEST");
					return message;
				}
			});

			TextMessage message = (TextMessage) jmsTemplate
					.receive("modulesManager.response");
			log.info("Received message: " + message.getText() + "\n");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			applicationContext.close();
		}
	}

}
