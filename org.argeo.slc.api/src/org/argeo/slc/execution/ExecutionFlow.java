package org.argeo.slc.execution;

import java.util.Iterator;

/** Abstraction of an execution that can be identified and configured. */
public interface ExecutionFlow extends Runnable {
	/** Retrieve an immutable parameter */
	public Object getParameter(String key);

	/** Whether this immutable parameter is set */
	public Boolean isSetAsParameter(String key);

	/** The specifications of the execution flow. */
	public ExecutionSpec getExecutionSpec();

	/**
	 * List sub-runnables that would be executed if run() method would be
	 * called.
	 */
	public Iterator<Runnable> runnables();

	/**
	 * If there is one and only one runnable wrapped return it, throw an
	 * exception otherwise.
	 */
	public Runnable getRunnable();

	/**
	 * The name of this execution flow. Can contains '/' which will be
	 * interpreted by UIs as a hierarchy;
	 */
	public String getName();
}
