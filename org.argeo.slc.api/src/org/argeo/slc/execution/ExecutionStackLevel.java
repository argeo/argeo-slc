package org.argeo.slc.execution;

import java.util.Map;

public interface ExecutionStackLevel {
	public ExecutionFlow getExecutionFlow();

	public Map<String, Object> getScopedObjects();

	public String getUuid();

	public Map<String, Object> getLocalVariables();

}
