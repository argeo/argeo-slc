package argeo.slc.activemq.selector;


public class OsgiTest extends AbstractActiveMqTest {
	public void testSend() throws Exception {
		producer.send(createMessage());
	}

	protected void setUp() throws Exception {
		connectorStr = "tcp://localhost:61616";
		createSession();
	}

	protected void tearDown() throws Exception {
		session.close();
	}

	/*
	 * public static void main(String[] args) { try { ConnectionFactory
	 * connectionFactory = new ActiveMQConnectionFactory(
	 * "tcp://localhost:61616"); Connection connection =
	 * connectionFactory.createConnection(); connection.start(); Session session
	 * = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	 * 
	 * Queue queue = session.createQueue("testQueue"); MessageProducer producer
	 * = session.createProducer(queue);
	 * 
	 * // TextMessage msg = session.createTextMessage(); //
	 * msg.setStringProperty(propName, propValue); // msg.setText(txt);
	 * 
	 * } catch (JMSException e) { e.printStackTrace(); }
	 * 
	 * }
	 */

}
