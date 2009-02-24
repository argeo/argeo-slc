package org.argeo.slc.execution;

import java.util.Map;

import org.argeo.slc.process.Executable;

public interface ExecutionFlow extends Executable{
	public Object getParameter(String name);
	public ExecutionSpec getExecutionSpec();
	public String getName();
}
