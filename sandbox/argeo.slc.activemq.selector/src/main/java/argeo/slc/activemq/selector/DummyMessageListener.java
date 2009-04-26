package argeo.slc.activemq.selector;

import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

public class DummyMessageListener implements MessageListener, InitializingBean {
	private final static Log log = LogFactory
			.getLog(DummyMessageListener.class);

	public void onMessage(Message message) {

		log.info("DummyMessageListener received message " + message);
	}

	public void afterPropertiesSet() throws Exception {
		log.info("DummyMessageListener configured.");

	}

}
