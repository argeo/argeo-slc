package org.argeo.api.slc.execution;

/** Variables or references attached to an execution (typically thread bounded).*/
public interface ExecutionContext {
	public final static String VAR_EXECUTION_CONTEXT_ID = "slcVar.executionContext.id";
	public final static String VAR_EXECUTION_CONTEXT_CREATION_DATE = "slcVar.executionContext.creationDate";
	public final static String VAR_FLOW_ID = "slcVar.flow.id";
	public final static String VAR_FLOW_NAME = "slcVar.flow.name";

	public String getUuid();

	/** @return the variable value, or <code>null</code> if not found. */
	public Object getVariable(String key);

	public void setVariable(String key, Object value);
	
	public void beforeFlow(ExecutionFlow executionFlow);
	
	public void afterFlow(ExecutionFlow executionFlow);
}
