package org.argeo.slc.ws.client;

import java.io.IOException;

import javax.xml.transform.Source;

import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.xml.validation.XmlValidator;
import org.xml.sax.SAXParseException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ValidatingClientInterceptor implements ClientInterceptor {
	private final static Log log = LogFactory
			.getLog(ValidatingClientInterceptor.class);

	private Boolean validateRequest = true;
	private Boolean validateResponse = false;
	private XmlValidator validator = null;

	public boolean handleFault(MessageContext messageContext)
			throws WebServiceClientException {
		return true;
	}

	public boolean handleRequest(MessageContext messageContext)
			throws WebServiceClientException {
		if (validateRequest) {
			if (messageContext.getRequest() == null)
				return true;

			Source source = messageContext.getRequest().getPayloadSource();
			try {
				return validate(source);
			} catch (IOException e) {
				throw new WebServiceIOException("Cannot validate request", e);
			}
		} else {
			return true;
		}
	}

	public boolean handleResponse(MessageContext messageContext)
			throws WebServiceClientException {
		if (validateResponse) {
			if (messageContext.getResponse() == null)
				return true;
			
			Source source = messageContext.getResponse().getPayloadSource();
			try {
				return validate(source);
			} catch (IOException e) {
				throw new WebServiceIOException("Cannot validate response", e);
			}
		} else {
			return true;
		}
	}

	protected boolean validate(Source source) throws IOException {
		SAXParseException[] exceptions = validator.validate(source);
		if (exceptions.length != 0) {
			for (SAXParseException ex : exceptions) {
				log.error(ex.getMessage());
			}
			return false;
		} else {
			return true;
		}
	}

	public void setValidateRequest(Boolean validateRequest) {
		this.validateRequest = validateRequest;
	}

	public void setValidateResponse(Boolean validateResponse) {
		this.validateResponse = validateResponse;
	}

	public void setValidator(XmlValidator validator) {
		this.validator = validator;
	}

}
