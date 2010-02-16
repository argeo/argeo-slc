package org.argeo.slc.core.execution;

import java.util.ArrayList;
import java.util.List;

import org.argeo.slc.execution.ExecutionModulesListener;
import org.argeo.slc.execution.ExecutionModulesManager;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.process.SlcExecutionNotifier;

public abstract class AbstractExecutionModulesManager implements
		ExecutionModulesManager {
	private List<SlcExecutionNotifier> slcExecutionNotifiers = new ArrayList<SlcExecutionNotifier>();
	private List<ExecutionModulesListener> executionModulesListeners = new ArrayList<ExecutionModulesListener>();

	private ThreadGroup processesThreadGroup = new ThreadGroup("Processes");

	public void process(SlcExecution slcExecution) {
		new ProcessThread(this, slcExecution).start();
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

	public List<ExecutionModulesListener> getExecutionModulesListeners() {
		return executionModulesListeners;
	}

	public void setExecutionModulesListeners(
			List<ExecutionModulesListener> executionModulesListeners) {
		this.executionModulesListeners = executionModulesListeners;
	}

}
