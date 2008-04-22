package org.argeo.slc.core.process;

import java.util.List;

import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;

import org.argeo.slc.msg.process.SlcExecutionRequest;
import org.argeo.slc.msg.process.SlcExecutionStepsRequest;

public class WebServiceSlcExecutionNotifier implements SlcExecutionNotifier {
	private WebServiceTemplate template;

	public void addSteps(SlcExecution slcExecution,
			List<SlcExecutionStep> additionalSteps) {
		SlcExecutionStepsRequest req = new SlcExecutionStepsRequest();
		req.setSlcExecutionUuid(slcExecution.getUuid());
		req.setSteps(additionalSteps);
		template.marshalSendAndReceive(req);
	}

	public void newExecution(SlcExecution slcExecution) {
		SlcExecutionRequest req = new SlcExecutionRequest();
		req.setSlcExecution(slcExecution);
		template.marshalSendAndReceive(req);
	}

	public void updateExecution(SlcExecution slcExecution) {
		SlcExecutionRequest req = new SlcExecutionRequest();
		req.setSlcExecution(slcExecution);
		template.marshalSendAndReceive(req);
	}

	public void setTemplate(WebServiceTemplate template) {
		this.template = template;
	}

}
