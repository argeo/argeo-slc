package org.argeo.slc.execution;

import java.util.Map;

public interface ExecutionContext {
	
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
	
	//TODO: replace with setVariable(String Key, Object value)
	public void addVariables(Map<? extends String, ? extends Object> variablesToAdd);
}
