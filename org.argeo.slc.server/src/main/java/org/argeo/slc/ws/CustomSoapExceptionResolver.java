package org.argeo.slc.ws;

import javax.xml.namespace.QName;

import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.server.endpoint.SimpleSoapExceptionResolver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CustomSoapExceptionResolver extends SimpleSoapExceptionResolver {
	private Log log = LogFactory.getLog(getClass());

	@Override
	protected void customizeFault(MessageContext messageContext,
			Object endpoint, Exception ex, SoapFault fault) {
		log.error("Exception " + ex.getMessage() + " in end point " + endpoint,
				ex);
		SoapFaultDetail detail = fault.addFaultDetail();
		for (StackTraceElement elem : ex.getStackTrace()) {
			detail.addFaultDetailElement(new QName("StackElement")).addText(
					elem.toString());
		}
	}

}
