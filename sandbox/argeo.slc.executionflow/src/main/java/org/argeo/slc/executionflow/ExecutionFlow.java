package org.argeo.slc.executionflow;

import java.util.Map;

import org.argeo.slc.process.Executable;

public interface ExecutionFlow extends Executable{
	public Map<String, Object> getAttributes();
	public ExecutionSpec getExecutionSpec();
	public String getUuid();
}
