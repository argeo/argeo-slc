package org.argeo.slc.client.ui.controllers;

import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.runtime.SlcAgent;
import org.argeo.slc.services.SlcExecutionService;

public class ProcessController {
	private SlcExecutionService slcExecutionService;

	public void execute(SlcAgent agent, SlcExecution slcExecution) {
		slcExecutionService.newExecution(slcExecution);
		agent.runSlcExecution(slcExecution);
	}

	public void setSlcExecutionService(SlcExecutionService slcExecutionService) {
		this.slcExecutionService = slcExecutionService;
	}

}
