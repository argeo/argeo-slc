package org.argeo.slc.execution;

public interface ExecutionStack {
	/**
	 * @param name
	 * @return null if no object is found
	 */
	public Object findScopedObject(String name);

	public void addScopedObject(String name, Object obj);

	public void enterFlow(ExecutionFlow executionFlow);

	/** @return internal stack level UUID. */
	public String getCurrentStackLevelUuid();

	public Integer getStackSize();

	public void leaveFlow(ExecutionFlow executionFlow);

	Object findLocalVariable(String key);
}
