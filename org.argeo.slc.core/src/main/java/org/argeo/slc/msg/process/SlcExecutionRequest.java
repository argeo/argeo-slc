package org.argeo.slc.msg.process;

import org.argeo.slc.core.process.SlcExecution;

public class SlcExecutionRequest {
	private SlcExecution slcExecution;

	public SlcExecutionRequest() {
	}

	public SlcExecutionRequest(SlcExecution slcExecution) {
		this.slcExecution = slcExecution;
	}

	public SlcExecution getSlcExecution() {
		return slcExecution;
	}

	public void setSlcExecution(SlcExecution slcExecution) {
		this.slcExecution = slcExecution;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "#" + slcExecution.getUuid();
	}
}
