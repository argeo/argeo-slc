package org.argeo.slc.jms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;
import org.springframework.jms.support.converter.MessageConverter;

public class AgentListenerContainer extends DefaultMessageListenerContainer
		implements InitializingBean {
	private final static Log log = LogFactory.getLog(AgentListenerContainer.class);
	
	private JmsAgent jmsAgent;
	private String action;
	private MessageConverter messageConverter;

	@Override
	public void afterPropertiesSet() {
		MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(
				jmsAgent);
		messageListenerAdapter.setDefaultListenerMethod(action);
		messageListenerAdapter.setMessageConverter(messageConverter);
		setMessageListener(messageListenerAdapter);
		setDestinationName(jmsAgent.actionDestinationName(action));
		super.afterPropertiesSet();
		
		log.info("Listening to "+getDestinationName());
	}

	public void setJmsAgent(JmsAgent jmsAgent) {
		this.jmsAgent = jmsAgent;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setMessageConverter(MessageConverter messageConverter) {
		this.messageConverter = messageConverter;
	}

}
