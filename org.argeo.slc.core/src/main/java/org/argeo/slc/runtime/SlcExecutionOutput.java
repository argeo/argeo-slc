package org.argeo.slc.runtime;

public interface SlcExecutionOutput<T extends SlcExecutionContext> {
	/** Called after the execution, before the resources are freed.*/
	public void postExecution(T executionContext);
}
