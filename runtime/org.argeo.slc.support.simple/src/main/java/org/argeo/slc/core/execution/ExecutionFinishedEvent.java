package org.argeo.slc.core.execution;

import org.argeo.slc.execution.ExecutionContext;
import org.springframework.context.ApplicationEvent;

public class ExecutionFinishedEvent extends ApplicationEvent {
	static final long serialVersionUID = 012;

	private final ExecutionContext executionContext;

	public ExecutionFinishedEvent(Object source, ExecutionContext executionContext) {
		super(source);
		this.executionContext = executionContext;
	}

	public ExecutionContext getExecutionContext() {
		return executionContext;
	}

}
