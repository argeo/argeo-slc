package org.argeo.slc.client.ui.controllers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.runtime.SlcAgent;
import org.argeo.slc.services.SlcExecutionService;

public class ProcessController {
	private final static Log log = LogFactory.getLog(ProcessController.class);
	private SlcExecutionService slcExecutionService;

	public void execute(SlcAgent agent, SlcExecution slcExecution) {
		slcExecutionService.newExecution(slcExecution);
		agent.runSlcExecution(slcExecution);
		if (log.isDebugEnabled())
			log.debug("SlcExcution " + slcExecution.getUuid()
					+ " launched on Agent " + agent.toString());
	}

	public void setSlcExecutionService(SlcExecutionService slcExecutionService) {
		this.slcExecutionService = slcExecutionService;
	}

}
