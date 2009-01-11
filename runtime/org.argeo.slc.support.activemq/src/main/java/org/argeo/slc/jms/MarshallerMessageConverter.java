package org.argeo.slc.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.argeo.slc.SlcException;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.xml.transform.StringResult;
import org.springframework.xml.transform.StringSource;

public class MarshallerMessageConverter implements MessageConverter {
	private Marshaller marshaller;
	private Unmarshaller unmarshaller;

	public Object fromMessage(Message message) throws JMSException,
			MessageConversionException {
		if (message instanceof TextMessage) {
			String text = ((TextMessage) message).getText();
			try {
				return unmarshaller.unmarshal(new StringSource(text));
			} catch (Exception e) {
				throw new SlcException("Could not unmarshall " + text, e);
			}
		} else {
			throw new SlcException("Only JMS TextMessage are supported.");
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
