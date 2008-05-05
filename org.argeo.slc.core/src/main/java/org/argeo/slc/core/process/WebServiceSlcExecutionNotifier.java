package org.argeo.slc.core.process;

import java.util.List;

import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.argeo.slc.msg.process.SlcExecutionRequest;
import org.argeo.slc.msg.process.SlcExecutionStatusRequest;
import org.argeo.slc.msg.process.SlcExecutionStepsRequest;
import org.argeo.slc.ws.client.WebServiceUtils;

public class WebServiceSlcExecutionNotifier implements SlcExecutionNotifier {
	private WebServiceTemplate template;

	private Log log = LogFactory.getLog(getClass());

	public void newExecution(SlcExecution slcExecution) {
		SlcExecutionRequest req = new SlcExecutionRequest();
		req.setSlcExecution(slcExecution);
		try {
			WebServiceUtils.marshalSendAndReceive(template, req);
			if (log.isTraceEnabled())
				log.trace("Notified creation of slc execution "
						+ slcExecution.getUuid());
		} catch (SoapFaultClientException e) {
			WebServiceUtils.manageSoapException(e);
		}
	}

	public void updateExecution(SlcExecution slcExecution) {
		SlcExecutionRequest req = new SlcExecutionRequest();
		req.setSlcExecution(slcExecution);
		try {
			WebServiceUtils.marshalSendAndReceive(template, req);
			if (log.isTraceEnabled())
				log.trace("Notified update of slc execution "
						+ slcExecution.getUuid());
		} catch (SoapFaultClientException e) {
			WebServiceUtils.manageSoapException(e);
		}
	}

	public void updateStatus(SlcExecution slcExecution, String oldStatus,
			String newStatus) {
		SlcExecutionStatusRequest req = new SlcExecutionStatusRequest(
				slcExecution.getUuid(), newStatus);
		try {
			WebServiceUtils.marshalSendAndReceive(template, req);
			if (log.isTraceEnabled())
				log.trace("Notified status update of slc execution "
						+ slcExecution.getUuid());
		} catch (SoapFaultClientException e) {
			WebServiceUtils.manageSoapException(e);
		}
	}

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
			WebServiceUtils.marshalSendAndReceive(template, req);
			if (log.isTraceEnabled())
				log.trace("Added steps to slc execution "
						+ slcExecution.getUuid());
		} catch (SoapFaultClientException e) {
			WebServiceUtils.manageSoapException(e);
		}
	}

	public void setTemplate(WebServiceTemplate template) {
		this.template = template;
	}

}
