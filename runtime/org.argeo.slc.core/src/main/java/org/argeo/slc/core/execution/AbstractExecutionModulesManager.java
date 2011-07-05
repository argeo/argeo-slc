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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.execution.ExecutionContext;
import org.argeo.slc.execution.ExecutionFlow;
import org.argeo.slc.execution.ExecutionFlowDescriptorConverter;
import org.argeo.slc.execution.ExecutionModulesManager;
import org.argeo.slc.execution.ExecutionProcess;
import org.argeo.slc.execution.ExecutionProcessNotifier;
import org.argeo.slc.execution.ExecutionStep;
import org.argeo.slc.process.RealizedFlow;
import org.argeo.slc.process.SlcExecutionNotifier;

/** Provides the base feature of an execution module manager. */
@SuppressWarnings("deprecation")
public abstract class AbstractExecutionModulesManager implements
		ExecutionModulesManager {
	private final static Log log = LogFactory
			.getLog(AbstractExecutionModulesManager.class);

	private List<SlcExecutionNotifier> slcExecutionNotifiers = new ArrayList<SlcExecutionNotifier>();

	private List<FilteredNotifier> filteredNotifiers = Collections
			.synchronizedList(new ArrayList<FilteredNotifier>());

	private ThreadGroup processesThreadGroup = new ThreadGroup("SLC Processes");

	protected abstract ExecutionFlow findExecutionFlow(String moduleName,
			String moduleVersion, String flowName);

	protected abstract ExecutionContext findExecutionContext(String moduleName,
			String moduleVersion);

	protected abstract ExecutionFlowDescriptorConverter getExecutionFlowDescriptorConverter(
			String moduleName, String moduleVersion);

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

	public void dispatchUpdateStatus(ExecutionProcess process,
			String oldStatus, String newStatus) {
		// generic notifiers (notified of everything)
		for (Iterator<SlcExecutionNotifier> it = getSlcExecutionNotifiers()
				.iterator(); it.hasNext();) {
			it.next().updateStatus(process, oldStatus, newStatus);
		}

		// filtered notifiers
		for (Iterator<FilteredNotifier> it = filteredNotifiers.iterator(); it
				.hasNext();) {
			FilteredNotifier filteredNotifier = it.next();
			if (filteredNotifier.receiveFrom(process))
				filteredNotifier.getNotifier().updateStatus(process, oldStatus,
						newStatus);
		}
	}

	public void dispatchAddSteps(ExecutionProcess process,
			List<ExecutionStep> steps) {
		for (Iterator<SlcExecutionNotifier> it = getSlcExecutionNotifiers()
				.iterator(); it.hasNext();) {
			it.next().addSteps(process, steps);
		}

		for (Iterator<FilteredNotifier> it = filteredNotifiers.iterator(); it
				.hasNext();) {
			FilteredNotifier filteredNotifier = it.next();
			if (filteredNotifier.receiveFrom(process))
				filteredNotifier.getNotifier().addSteps(process, steps);
		}
	}

	public void registerProcessNotifier(ExecutionProcessNotifier notifier,
			Map<String, String> properties) {
		filteredNotifiers.add(new FilteredNotifier(notifier, properties));
	}

	public void setSlcExecutionNotifiers(
			List<SlcExecutionNotifier> slcExecutionNotifiers) {
		this.slcExecutionNotifiers = slcExecutionNotifiers;
	}

	private List<SlcExecutionNotifier> getSlcExecutionNotifiers() {
		return slcExecutionNotifiers;
	}

	public ThreadGroup getProcessesThreadGroup() {
		return processesThreadGroup;
	}

	protected class FilteredNotifier {
		private final ExecutionProcessNotifier notifier;
		private final String processId;

		public FilteredNotifier(ExecutionProcessNotifier notifier,
				Map<String, String> properties) {
			super();
			this.notifier = notifier;
			if (properties.containsKey(SLC_PROCESS_ID))
				processId = properties.get(SLC_PROCESS_ID);
			else
				processId = null;
		}

		/**
		 * Whether event from this process should be received by this listener.
		 */
		public Boolean receiveFrom(ExecutionProcess process) {
			if (processId != null)
				if (process.getUuid().equals(processId))
					return true;
				else
					return false;
			return true;
		}

		@Override
		public int hashCode() {
			return notifier.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			return notifier.equals(obj);
		}

		public ExecutionProcessNotifier getNotifier() {
			return notifier;
		}

	}
}
