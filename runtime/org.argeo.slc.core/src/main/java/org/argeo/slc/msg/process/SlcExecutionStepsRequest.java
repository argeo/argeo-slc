/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.argeo.slc.msg.process;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.argeo.slc.execution.ExecutionStep;
import org.argeo.slc.process.SlcExecutionStep;

@Deprecated
public class SlcExecutionStepsRequest implements Serializable {
	private static final long serialVersionUID = 6243880315234605390L;
	private String slcExecutionUuid;
	private List<SlcExecutionStep> steps = new ArrayList<SlcExecutionStep>();

	public SlcExecutionStepsRequest() {

	}

	public SlcExecutionStepsRequest(String slcExecutionUuid,
			List<ExecutionStep> steps) {
		this.slcExecutionUuid = slcExecutionUuid;
		for (ExecutionStep step : steps) {
			this.steps.add((SlcExecutionStep) step);
		}
	}

	public SlcExecutionStepsRequest(String slcExecutionUuid, ExecutionStep step) {
		this.slcExecutionUuid = slcExecutionUuid;
		List<SlcExecutionStep> steps = new ArrayList<SlcExecutionStep>();
		steps.add((SlcExecutionStep) step);
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
