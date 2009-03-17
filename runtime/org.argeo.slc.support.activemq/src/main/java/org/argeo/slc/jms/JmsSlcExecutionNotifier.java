package org.argeo.slc.jms;

import java.util.List;

import javax.jms.Destination;

import org.argeo.slc.SlcException;
import org.argeo.slc.UnsupportedException;
import org.argeo.slc.msg.process.SlcExecutionStatusRequest;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.process.SlcExecutionNotifier;
import org.argeo.slc.process.SlcExecutionStep;
import org.springframework.jms.core.JmsTemplate;

public class JmsSlcExecutionNotifier implements SlcExecutionNotifier {

	private JmsTemplate jmsTemplate;

	private Destination updateStatusDestination;

	public void updateStatus(SlcExecution slcExecution, String oldStatus,
			String newStatus) {
		SlcExecutionStatusRequest req = new SlcExecutionStatusRequest(
				slcExecution.getUuid(), newStatus);
		jmsTemplate.convertAndSend(updateStatusDestination, req);
	}

	public void addSteps(SlcExecution slcExecution,
			List<SlcExecutionStep> additionalSteps) {
		throw new UnsupportedException();
	}

	public void newExecution(SlcExecution slcExecution) {
		throw new UnsupportedException();
	}

	public void updateExecution(SlcExecution slcExecution) {
		throw new UnsupportedException();
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	public void setUpdateStatusDestination(Destination updateStatusDestination) {
		this.updateStatusDestination = updateStatusDestination;
	}

}
