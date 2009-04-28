package org.argeo.slc.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.argeo.slc.SlcException;
import org.argeo.slc.msg.MsgHandler;
import org.springframework.jms.support.converter.MessageConverter;

public class JmsExecutionHandler implements MessageListener {

	private MessageConverter messageConverter;
	private MsgHandler serviceMsgHandler;

	public void onMessage(Message message) {
		try {
			serviceMsgHandler.handleMsg(messageConverter.fromMessage(message));
		} catch (JMSException e) {
			throw new SlcException("Could not interpret message " + message, e);
		}
	}

	public void setMessageConverter(MessageConverter messageConverter) {
		this.messageConverter = messageConverter;
	}

	public void setServiceMsgHandler(MsgHandler serviceMsgHandler) {
		this.serviceMsgHandler = serviceMsgHandler;
	}

}
