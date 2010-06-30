/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
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

import java.util.Enumeration;

import javax.jms.JMSException;
import javax.jms.Message;
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

	/** @return the converted message or null if the message itself is null */
	@SuppressWarnings("unchecked")
	public Object fromMessage(Message message) throws JMSException,
			MessageConversionException {
		if (message == null) {
			return null;
		}

		if (log.isTraceEnabled()) {
			Enumeration<String> names = message.getPropertyNames();
			while (names.hasMoreElements()) {
				String name = names.nextElement();
				log.trace("JMS Property: " + name + "="
						+ message.getObjectProperty(name));
			}
		}

		if (message instanceof TextMessage) {

			String text = ((TextMessage) message).getText();

			if (text == null)
				throw new SlcException(
						"Cannot unmarshall message without body: " + message);

			try {
				return unmarshaller.unmarshal(new StringSource(text));
			} catch (Exception e) {
				throw new SlcException("Could not unmarshall " + text, e);
			}
		} else {
			throw new SlcException("This type of messages is not supported: "
					+ message);
		}
	}

	public Message toMessage(Object object, Session session)
			throws JMSException, MessageConversionException {
		StringResult result = new StringResult();
		try {
			marshaller.marshal(object, result);
		} catch (Exception e) {
			throw new SlcException("Could not marshall " + object, e);
		}
		TextMessage msg = session.createTextMessage();
		msg.setText(result.toString());
		return msg;
	}

	public void setMarshaller(Marshaller marshaller) {
		this.marshaller = marshaller;
	}

	public void setUnmarshaller(Unmarshaller unmarshaller) {
		this.unmarshaller = unmarshaller;
	}

}
