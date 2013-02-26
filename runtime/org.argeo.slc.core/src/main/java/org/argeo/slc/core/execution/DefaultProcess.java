package org.argeo.slc.core.execution;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.argeo.slc.execution.ExecutionProcess;
import org.argeo.slc.execution.ExecutionStep;
import org.argeo.slc.execution.RealizedFlow;

public class DefaultProcess implements ExecutionProcess {
	private String uuid = UUID.randomUUID().toString();
	private String status;

	private List<ExecutionStep> steps = new ArrayList<ExecutionStep>();

	public String getUuid() {
		return uuid;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void addSteps(List<ExecutionStep> steps) {
		steps.addAll(steps);
	}

	public List<RealizedFlow> getRealizedFlows() {
		// TODO Auto-generated method stub
		return null;
	}

}
