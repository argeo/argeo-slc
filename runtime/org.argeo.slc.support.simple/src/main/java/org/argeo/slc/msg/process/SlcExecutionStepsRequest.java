package org.argeo.slc.msg.process;

import java.util.List;
import java.util.Vector;

import org.argeo.slc.process.SlcExecutionStep;

public class SlcExecutionStepsRequest {
	private String slcExecutionUuid;
	private List<SlcExecutionStep> steps = new Vector<SlcExecutionStep>();

	public SlcExecutionStepsRequest() {

	}

	public SlcExecutionStepsRequest(String slcExecutionUuid,
			List<SlcExecutionStep> steps) {
		this.slcExecutionUuid = slcExecutionUuid;
		this.steps = steps;
	}

	public SlcExecutionStepsRequest(String slcExecutionUuid,
			SlcExecutionStep step) {
		this.slcExecutionUuid = slcExecutionUuid;
		List<SlcExecutionStep> steps = new Vector<SlcExecutionStep>();
		steps.add(step);
		this.steps = steps;
	}

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

	@Override
	public String toString() {
		return getClass().getSimpleName() + "#" + slcExecutionUuid + " "
				+ steps;
	}
}
