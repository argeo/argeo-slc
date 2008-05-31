package org.argeo.slc.ws.process;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.core.process.SlcExecution;
import org.argeo.slc.core.process.SlcExecutionNotifier;
import org.argeo.slc.core.process.SlcExecutionStep;
import org.argeo.slc.msg.process.SlcExecutionRequest;
import org.argeo.slc.msg.process.SlcExecutionStatusRequest;
import org.argeo.slc.msg.process.SlcExecutionStepsRequest;
import org.argeo.slc.ws.client.WebServiceUtils;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;

public class WebServiceSlcExecutionNotifier implements SlcExecutionNotifier {
	private WebServiceTemplate template;

	private Log log = LogFactory.getLog(getClass());

	private Boolean cannotConnect = false;

	public void newExecution(SlcExecution slcExecution) {
		if (cannotConnect)
			return;

		SlcExecutionRequest req = new SlcExecutionRequest();
		req.setSlcExecution(slcExecution);
		try {
			WebServiceUtils.marshalSendAndReceive(template, req);
			if (log.isTraceEnabled())
				log.trace("Notified creation of slc execution "
						+ slcExecution.getUuid());
		} catch (SoapFaultClientException e) {
			WebServiceUtils.manageSoapException(e);
		} catch (WebServiceIOException e) {
			manageIoException(e);
		}
	}

	public void updateExecution(SlcExecution slcExecution) {
		if (cannotConnect)
			return;

		SlcExecutionRequest req = new SlcExecutionRequest();
		req.setSlcExecution(slcExecution);
		try {
			WebServiceUtils.marshalSendAndReceive(template, req);
			if (log.isTraceEnabled())
				log.trace("Notified update of slc execution "
						+ slcExecution.getUuid());
		} catch (SoapFaultClientException e) {
			WebServiceUtils.manageSoapException(e);
		} catch (WebServiceIOException e) {
			manageIoException(e);
		}
	}

	public void updateStatus(SlcExecution slcExecution, String oldStatus,
			String newStatus) {
		if (cannotConnect)
			return;

		SlcExecutionStatusRequest req = new SlcExecutionStatusRequest(
				slcExecution.getUuid(), newStatus);
		try {
			WebServiceUtils.marshalSendAndReceive(template, req);
			if (log.isTraceEnabled())
				log.trace("Notified status update of slc execution "
						+ slcExecution.getUuid());
		} catch (SoapFaultClientException e) {
			WebServiceUtils.manageSoapException(e);
		} catch (WebServiceIOException e) {
			manageIoException(e);
		}
	}

	public void addSteps(SlcExecution slcExecution,
			List<SlcExecutionStep> additionalSteps) {
		if (cannotConnect)
			return;

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
		} catch (WebServiceIOException e) {
			manageIoException(e);
		}
	}

	public void setTemplate(WebServiceTemplate template) {
		this.template = template;
	}

	protected void manageIoException(WebServiceIOException e) {
		if (!cannotConnect) {
			log.error("Cannot connect to " + template.getDefaultUri()
					+ ". Won't try again.", e);
			cannotConnect = true;
		}
	}

}
