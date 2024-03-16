package org.argeo.slc.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.argeo.api.slc.execution.ExecutionProcess;
import org.argeo.api.slc.execution.ExecutionStep;
import org.argeo.api.slc.execution.RealizedFlow;

/** Canonical implementation of an {@link ExecutionProcess} as a bean. */
public class DefaultProcess implements ExecutionProcess {
	private String uuid = UUID.randomUUID().toString();
	private String status = ExecutionProcess.NEW;

	private List<ExecutionStep> steps = new ArrayList<ExecutionStep>();
	private List<RealizedFlow> realizedFlows = new ArrayList<RealizedFlow>();

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
		return realizedFlows;
	}

	public List<ExecutionStep> getSteps() {
		return steps;
	}

	public void setSteps(List<ExecutionStep> steps) {
		this.steps = steps;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public void setRealizedFlows(List<RealizedFlow> realizedFlows) {
		this.realizedFlows = realizedFlows;
	}

}
