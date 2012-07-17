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
package org.argeo.slc.ws.client;

import java.util.Iterator;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.SoapFaultDetailElement;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.springframework.xml.transform.StringResult;
import org.w3c.dom.Node;

public abstract class WebServiceUtils {
	private final static Log log = LogFactory.getLog(WebServiceUtils.class);

	public static Object marshalSendAndReceiveSafe(WebServiceTemplate template,
			Object req) {
		try {
			Object resp = marshalSendAndReceive(template, req);
			return resp;
		} catch (Exception e) {
			log.error("Cannot send web servicerequest: " + e.getMessage());
			if (log.isDebugEnabled()) {
				log.debug("Webservice exception details: ", e);
			}
			return null;
		}
	}

	public static Object marshalSendAndReceive(WebServiceTemplate template,
			Object req) {
		if (log.isTraceEnabled()) {
			try {
				StringResult result = new StringResult();
				template.getMarshaller().marshal(req, result);
				log.trace("About to send " + result);
			} catch (Exception e) {
				log.error("Cannot marshall " + req + " for logging", e);
			}
		}
		Object resp = template.marshalSendAndReceive(req);
		return resp;
	}

	public static void manageSoapException(SoapFaultClientException e) {
		log
				.error("WS root cause: "
						+ e.getSoapFault().getFaultStringOrReason());
		if (log.isTraceEnabled()) {
			StringBuffer stack = new StringBuffer("");
			SoapFaultDetail detail = e.getSoapFault().getFaultDetail();
			if (detail != null) {
				Iterator<SoapFaultDetailElement> it = (Iterator<SoapFaultDetailElement>) detail
						.getDetailEntries();
				while (it.hasNext()) {
					SoapFaultDetailElement elem = it.next();
					if (elem.getName().getLocalPart().equals("StackElement")) {
						Source source = elem.getSource();
						if (source instanceof DOMSource) {
							Node node = ((DOMSource) source).getNode();
							// stack.append(node.getTextContent()).append('\n');
							stack
									.append(
											node.getChildNodes().item(0)
													.getNodeValue()).append(
											'\n');
						}
					}
				}

				if (stack.length() > 0)
					log.error("WS root cause stack: " + stack);
			}
		}
	}

	private WebServiceUtils() {

	}
}
