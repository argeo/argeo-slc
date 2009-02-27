package org.argeo.slc.execution;

import org.argeo.slc.process.Executable;

public interface ExecutionFlow extends Executable {
	public Object getParameter(String key);

	public Boolean isSetAsParameter(String key);

	public ExecutionSpec getExecutionSpec();

	public String getName();
}
