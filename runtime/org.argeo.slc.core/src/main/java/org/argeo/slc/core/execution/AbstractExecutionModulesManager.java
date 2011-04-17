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
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.execution.ExecutionContext;
import org.argeo.slc.execution.ExecutionFlow;
import org.argeo.slc.execution.ExecutionFlowDescriptorConverter;
import org.argeo.slc.execution.ExecutionModulesManager;
import org.argeo.slc.process.RealizedFlow;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.process.SlcExecutionNotifier;
import org.argeo.slc.process.SlcExecutionStep;

/** Provides the base feature of an execution module manager. */
public abstract class AbstractExecutionModulesManager implements
		ExecutionModulesManager {
	private final static Log log = LogFactory
			.getLog(AbstractExecutionModulesManager.class);

	private List<SlcExecutionNotifier> slcExecutionNotifiers = new ArrayList<SlcExecutionNotifier>();

	private ThreadGroup processesThreadGroup = new ThreadGroup("Processes");

	protected abstract ExecutionFlow findExecutionFlow(String moduleName,
			String moduleVersion, String flowName);

	protected abstract ExecutionContext findExecutionContext(String moduleName,
			String moduleVersion);

	protected abstract ExecutionFlowDescriptorConverter getExecutionFlowDescriptorConverter(
			String moduleName, String moduleVersion);

	public void process(SlcExecution slcExecution) {
		new ProcessThread(this, slcExecution).start();
	}

	public void execute(RealizedFlow realizedFlow) {
		if (log.isTraceEnabled())
			log.trace("Executing " + realizedFlow);

		String moduleName = realizedFlow.getModuleName();
		String moduleVersion = realizedFlow.getModuleVersion();

		Map<? extends String, ? extends Object> variablesToAdd = getExecutionFlowDescriptorConverter(
				moduleName, moduleVersion).convertValues(
				realizedFlow.getFlowDescriptor());
		ExecutionContext executionContext = findExecutionContext(moduleName,
				moduleVersion);
		for (String key : variablesToAdd.keySet())
			executionContext.setVariable(key, variablesToAdd.get(key));

		ExecutionFlow flow = findExecutionFlow(moduleName, moduleVersion,
				realizedFlow.getFlowDescriptor().getName());

		//
		// Actually runs the flow, IN THIS THREAD
		//
		flow.run();
		//
		//
		//
	}

	public void dispatchUpdateStatus(SlcExecution slcExecution,
			String oldStatus, String newStatus) {
		for (Iterator<SlcExecutionNotifier> it = getSlcExecutionNotifiers()
				.iterator(); it.hasNext();) {
			it.next().updateStatus(slcExecution, oldStatus, newStatus);
		}
	}

	public void dispatchAddStep(SlcExecution slcExecution, SlcExecutionStep step) {
		List<SlcExecutionStep> steps = new ArrayList<SlcExecutionStep>();
		steps.add(step);
		for (Iterator<SlcExecutionNotifier> it = getSlcExecutionNotifiers()
				.iterator(); it.hasNext();) {
			it.next().addSteps(slcExecution, steps);
		}
	}

	public void setSlcExecutionNotifiers(
			List<SlcExecutionNotifier> slcExecutionNotifiers) {
		this.slcExecutionNotifiers = slcExecutionNotifiers;
	}

	public List<SlcExecutionNotifier> getSlcExecutionNotifiers() {
		return slcExecutionNotifiers;
	}

	public ThreadGroup getProcessesThreadGroup() {
		return processesThreadGroup;
	}

}
