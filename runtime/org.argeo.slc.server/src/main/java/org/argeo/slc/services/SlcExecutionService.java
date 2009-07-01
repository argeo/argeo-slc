package org.argeo.slc.services;

import org.argeo.slc.msg.process.SlcExecutionStatusRequest;
import org.argeo.slc.msg.process.SlcExecutionStepsRequest;
import org.argeo.slc.process.SlcExecution;

public interface SlcExecutionService {
	public void newExecution(SlcExecution slcExecutionMsg);

	public void updateStatus(SlcExecutionStatusRequest msg);

	public void addSteps(SlcExecutionStepsRequest msg);
}
