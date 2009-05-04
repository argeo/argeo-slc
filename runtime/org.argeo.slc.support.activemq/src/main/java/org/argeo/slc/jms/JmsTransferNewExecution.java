package org.argeo.slc.jms;

import javax.jms.Message;
import javax.jms.MessageListener;

import org.argeo.slc.SlcException;
import org.argeo.slc.core.runtime.AbstractAgent;
import org.argeo.slc.msg.MsgHandler;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.runtime.SlcAgent;
import org.argeo.slc.runtime.SlcAgentFactory;
import org.springframework.jms.support.converter.MessageConverter;

/** Temporary hack */
public class JmsTransferNewExecution implements MessageListener {
	private MessageConverter messageConverter;
	private SlcAgentFactory agentFactory;
	private MsgHandler serviceMsgHandler;

	public void onMessage(final Message message) {
		try {
			String agentId = message
					.getStringProperty(AbstractAgent.PROPERTY_SLC_AGENT_ID);
			final SlcAgent agent = agentFactory.getAgent(agentId);
			final SlcExecution slcExecution = (SlcExecution) messageConverter
					.fromMessage(message);
			new Thread() {
				public void run() {
					agent.runSlcExecution(slcExecution);
				}
			}.start();
			serviceMsgHandler.handleMsg(slcExecution);
		} catch (Exception e) {
			throw new SlcException("Could not transfer new execution "
					+ message, e);
		}
	}

	public void setMessageConverter(MessageConverter messageConverter) {
		this.messageConverter = messageConverter;
	}

	public void setAgentFactory(SlcAgentFactory agentFactory) {
		this.agentFactory = agentFactory;
	}

	public void setServiceMsgHandler(MsgHandler serviceMsgHandler) {
		this.serviceMsgHandler = serviceMsgHandler;
	}

}
