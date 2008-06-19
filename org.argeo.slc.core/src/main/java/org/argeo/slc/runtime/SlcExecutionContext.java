package org.argeo.slc.runtime;

import org.argeo.slc.core.process.SlcExecution;

/** Provides access to the object used during the execution */
public interface SlcExecutionContext {
	public Object getBean(String name);

	public SlcExecution getSlcExecution();
}
