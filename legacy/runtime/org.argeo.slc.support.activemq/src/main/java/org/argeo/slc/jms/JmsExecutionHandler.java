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
