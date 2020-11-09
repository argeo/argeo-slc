package org.argeo.slc.execution;

/**
 * Interpret a command line and run it in the underlying agent, with the proper
 * authentication.
 */
public interface SlcAgentCli {
	/**
	 * Synchronously executes.
	 * 
	 * @return the UUID of the process
	 */
	public String process(String[] args);
}
