package org.argeo.slc.msg.process;

import java.util.List;
import java.util.Vector;

import org.argeo.slc.core.process.SlcExecutionStep;

public class SlcExecutionStepsRequest {
	private String slcExecutionUuid;
	private List<SlcExecutionStep> steps = new Vector<SlcExecutionStep>();

	public String getSlcExecutionUuid() {
		return slcExecutionUuid;
	}

	public void setSlcExecutionUuid(String slcExecutionUuid) {
		this.slcExecutionUuid = slcExecutionUuid;
	}

	public List<SlcExecutionStep> getSteps() {
		return steps;
	}

	public void setSteps(List<SlcExecutionStep> step) {
		this.steps = step;
	}

	public void addStep(SlcExecutionStep step) {
		steps.add(step);
	}
}
