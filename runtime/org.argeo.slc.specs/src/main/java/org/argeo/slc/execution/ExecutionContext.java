package org.argeo.slc.execution;

import java.util.Date;
import java.util.Map;

public interface ExecutionContext {
	public final static String VAR_PROCESS_ID = "slcVar.process.id";
	public final static String VAR_EXECUTION_CONTEXT_ID = "slcVar.executionContext.id";
	public final static String VAR_FLOW_ID = "slcVar.flow.id";
	public final static String VAR_FLOW_NAME = "slcVar.flow.name";

	/**
	 * @param name
	 * @return null if no object is found
	 */
	public Object findScopedObject(String name);

	public void addScopedObject(String name, Object obj);

	public String getUuid();

	public void enterFlow(ExecutionFlow executionFlow);

	public void leaveFlow(ExecutionFlow executionFlow);

	public Object getVariable(String key);

	public Object findVariable(String key);

	// TODO: replace with setVariable(String Key, Object value)
	public void addVariables(
			Map<? extends String, ? extends Object> variablesToAdd);

	public Date getCreationDate();
}
