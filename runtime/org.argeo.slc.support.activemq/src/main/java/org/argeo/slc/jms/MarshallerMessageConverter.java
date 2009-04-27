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

	public Object fromMessage(Message message) throws JMSException,
			MessageConversionException {
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
