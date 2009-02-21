package org.argeo.slc.execution;

import org.argeo.slc.process.SlcExecution;
import org.springframework.context.ApplicationEvent;

public class NewExecutionEvent extends ApplicationEvent {
	static final long serialVersionUID = 012;

	private final SlcExecution slcExecution;

	public NewExecutionEvent(Object source, SlcExecution slcExecution) {
		super(source);
		this.slcExecution = slcExecution;
	}

	public SlcExecution getSlcExecution() {
		return slcExecution;
	}

}
