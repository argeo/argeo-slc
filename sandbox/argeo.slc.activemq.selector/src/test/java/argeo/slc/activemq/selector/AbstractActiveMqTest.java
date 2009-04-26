package argeo.slc.activemq.selector;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import junit.framework.TestCase;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;

public abstract class AbstractActiveMqTest extends TestCase {
	// private final static Log log = LogFactory.getLog(SelectorTest.class);

	protected BrokerService broker;
	protected String connectorStr = "vm://localhost";

	protected String propName = "myFilter";
	protected String propValue = "myValue";
	protected String txt = "myText";

	protected Session session;
	protected Queue queue;
	protected MessageProducer producer;

	protected void createSession() throws Exception {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
				connectorStr);
		Connection connection = connectionFactory.createConnection();
		connection.start();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		queue = session.createQueue("testQueue");
		producer = session.createProducer(queue);
	}

	protected TextMessage createMessage() throws Exception {
		TextMessage msg = session.createTextMessage();
		msg.setStringProperty(propName, propValue);
		msg.setText(txt);
		return msg;
	}

	protected void assertMsg(TextMessage receivedMsg) throws Exception {
		assertNotNull(receivedMsg);
		assertEquals(propValue, receivedMsg.getStringProperty(propName));
		assertEquals(txt, receivedMsg.getText());
	}

}
