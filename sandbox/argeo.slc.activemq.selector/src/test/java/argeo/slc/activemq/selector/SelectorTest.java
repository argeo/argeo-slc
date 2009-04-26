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

public class SelectorTest extends AbstractActiveMqTest {
	// private final static Log log = LogFactory.getLog(SelectorTest.class);

	protected BrokerService broker;
	protected String connectorStr = "vm://localhost";

	protected String propName = "myFilter";
	protected String propValue = "myValue";
	protected String txt = "myText";

	protected Session session;
	protected Queue queue;
	protected MessageProducer producer;

	public void testNoFilter() throws Exception {
		producer.send(createMessage());
		TextMessage receivedMsg = (TextMessage) session.createConsumer(queue)
				.receive(2000);
		assertMsg(receivedMsg);
	}

	public void testFilterOk() throws Exception {
		producer.send(createMessage());
		MessageConsumer consumer = session.createConsumer(queue, propName
				+ "='" + propValue + "'");
		TextMessage receivedMsg = (TextMessage) consumer.receive(2000);
		assertMsg(receivedMsg);
	}

	public void testFilterNok() throws Exception {
		producer.send(createMessage());
		MessageConsumer consumer = session.createConsumer(queue, propName
				+ "='notMyValue'");
		TextMessage receivedMsg = (TextMessage) consumer.receive(1000);
		assertNull("Message reception should time out", receivedMsg);
	}

	protected void setUp() throws Exception {
		broker = new BrokerService();
		broker.setPersistent(false);
		broker.setUseJmx(false);
		broker.addConnector(connectorStr);
		broker.start();

		createSession();
	}

	protected void tearDown() throws Exception {
		if (broker != null) {
			broker.stop();
		}
	}

}
