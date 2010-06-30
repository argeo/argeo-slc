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

package org.argeo.slc.core.execution;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.process.SlcExecutionNotifier;
import org.argeo.slc.process.SlcExecutionStep;

public class ProcessThreadGroup extends ThreadGroup {
	private final ProcessThread processThread;

	public ProcessThreadGroup(ProcessThread processThread) {
		super("SLC Process #" + processThread.getSlcProcess().getUuid()
				+ " thread group");
		this.processThread = processThread;
	}

	public SlcExecution getSlcProcess() {
		return processThread.getSlcProcess();
	}

	public void dispatchAddStep(SlcExecutionStep step) {
		processThread.getSlcProcess().getSteps().add(step);
		List<SlcExecutionStep> steps = new ArrayList<SlcExecutionStep>();
		steps.add(step);
		for (Iterator<SlcExecutionNotifier> it = processThread
				.getExecutionModulesManager().getSlcExecutionNotifiers()
				.iterator(); it.hasNext();) {
			it.next().addSteps(processThread.getSlcProcess(), steps);
		}
	}

}
