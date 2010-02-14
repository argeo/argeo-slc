package org.argeo.slc.execution;

public interface ExecutionFlow extends Runnable {
	public Object getParameter(String key);

	public Boolean isSetAsParameter(String key);

	public ExecutionSpec getExecutionSpec();

	public String getName();

	public String getPath();

	/**
	 * Actually performs the execution of the Runnable. This method should never
	 * be called directly. The implementation should provide a reasonable
	 * default, but it is meant to be intercepted either to analyze what is run
	 * or to override the default behavior.
	 */
	public void doExecuteRunnable(Runnable runnable);
}
