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
