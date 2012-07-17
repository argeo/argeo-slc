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

import java.io.Serializable;
import java.util.Enumeration;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.xml.transform.StringResult;
import org.springframework.xml.transform.StringSource;

public class MarshallerMessageConverter implements MessageConverter {
	private final static Log log = LogFactory
			.getLog(MarshallerMessageConverter.class);

	private Marshaller marshaller;
	private Unmarshaller unmarshaller;

	/** Use unmarshalled ObjectMessages instead of TextMessages */
	private Boolean disableMarshalling = false;

	/** @return the converted message or null if the message itself is null */
	@SuppressWarnings("unchecked")
	public Object fromMessage(Message message) throws JMSException,
			MessageConversionException {
		long begin = System.currentTimeMillis();
		if (message == null)
			return null;
		if (log.isTraceEnabled()) {
			Enumeration<String> names = message.getPropertyNames();
			while (names.hasMoreElements()) {
				String name = names.nextElement();
				log.trace("JMS Property: " + name + "="
						+ message.getObjectProperty(name));
			}
		}

		Object res;
		if (message instanceof TextMessage) {
			String text = ((TextMessage) message).getText();
			if (text == null)
				throw new SlcException(
						"Cannot unmarshall message without body: " + message);
			try {
				res = unmarshaller.unmarshal(new StringSource(text));
			} catch (Exception e) {
				throw new SlcException("Could not unmarshall " + text, e);
			}
		} else if (message instanceof ObjectMessage) {
			res = ((ObjectMessage) message).getObject();
			if (res == null)
				throw new SlcException("Message without body: " + message);
		} else {
			throw new SlcException("This type of messages is not supported: "
					+ message);
		}
		if (log.isTraceEnabled())
			log.trace("'From' message processed in " + (System.currentTimeMillis() - begin)
					+ " ms");
		return res;
	}

	public Message toMessage(Object object, Session session)
			throws JMSException, MessageConversionException {
		long begin = System.currentTimeMillis();
		Message msg;
		if (disableMarshalling) {
			msg = session.createObjectMessage();
			((ObjectMessage) msg).setObject((Serializable) object);
		} else {
			StringResult result = new StringResult();
			try {
				marshaller.marshal(object, result);
			} catch (Exception e) {
				throw new SlcException("Could not marshall " + object, e);
			}
			msg = session.createTextMessage();
			((TextMessage) msg).setText(result.toString());
		}
		if (log.isTraceEnabled())
			log.trace("'To' message processed in " + (System.currentTimeMillis() - begin)
					+ " ms");
		return msg;
	}

	public void setMarshaller(Marshaller marshaller) {
		this.marshaller = marshaller;
	}

	public void setUnmarshaller(Unmarshaller unmarshaller) {
		this.unmarshaller = unmarshaller;
	}

	public void setDisableMarshalling(Boolean disableMarshalling) {
		this.disableMarshalling = disableMarshalling;
	}

}
