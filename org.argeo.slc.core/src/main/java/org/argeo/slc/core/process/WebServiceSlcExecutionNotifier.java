package org.argeo.slc.core.process;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.springframework.oxm.XmlMappingException;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.SoapFaultDetailElement;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.springframework.xml.transform.StringResult;
import org.w3c.dom.Node;

import com.ibm.wsdl.util.IOUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.argeo.slc.msg.process.SlcExecutionRequest;
import org.argeo.slc.msg.process.SlcExecutionStepsRequest;

public class WebServiceSlcExecutionNotifier implements SlcExecutionNotifier {
	private WebServiceTemplate template;

	private Log log = LogFactory.getLog(getClass());

	public void addSteps(SlcExecution slcExecution,
			List<SlcExecutionStep> additionalSteps) {
		SlcExecutionStepsRequest req = new SlcExecutionStepsRequest();
		req.setSlcExecutionUuid(slcExecution.getUuid());
		req.setSteps(additionalSteps);
		if (log.isTraceEnabled()) {
			for (SlcExecutionStep step : additionalSteps) {
				log.trace("Step " + step.getUuid() + ": " + step.logAsString());
			}
		}

		try {
			marshalSendAndReceive(req);
			if (log.isDebugEnabled())
				log.debug("Added steps to slc execution "
						+ slcExecution.getUuid());
		} catch (SoapFaultClientException e) {
			manageSoapException(e);
		}
	}

	public void newExecution(SlcExecution slcExecution) {
		SlcExecutionRequest req = new SlcExecutionRequest();
		req.setSlcExecution(slcExecution);
		try {
			marshalSendAndReceive(req);
			if (log.isDebugEnabled())
				log.debug("Notified creation of slc execution "
						+ slcExecution.getUuid());
		} catch (SoapFaultClientException e) {
			manageSoapException(e);
		}
	}

	public void updateExecution(SlcExecution slcExecution) {
		SlcExecutionRequest req = new SlcExecutionRequest();
		req.setSlcExecution(slcExecution);
		try {
			marshalSendAndReceive(req);
			if (log.isDebugEnabled())
				log.debug("Notified update of slc execution "
						+ slcExecution.getUuid());
		} catch (SoapFaultClientException e) {
			manageSoapException(e);
		}
	}

	public void setTemplate(WebServiceTemplate template) {
		this.template = template;
	}

	protected void manageSoapException(SoapFaultClientException e) {
		log
				.error("WS root cause: "
						+ e.getSoapFault().getFaultStringOrReason());
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
						stack.append(node.getTextContent()).append('\n');
					}
				}
			}

			if (stack.length() > 0 && log.isTraceEnabled())
				log.error("WS root cause stack: " + stack);
		}
	}

	protected Object marshalSendAndReceive(Object req) {
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
}
