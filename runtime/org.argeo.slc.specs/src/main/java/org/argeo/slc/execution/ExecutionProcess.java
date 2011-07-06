package org.argeo.slc.execution;

/**
 * A process is the functional representation of a combination of executions.
 * While an execution is the actual java code running, a process exists before,
 * during and after the execution actually took place, providing an entry point
 * for the definition of executions, their monitoring (e.g. logging) and
 * tracking. A process can be distributed or parallelized.
 * <br/>
 * NEW => INITIALIZED => SCHEDULED => RUNNING<br/>
 * RUNNING => {COMPLETED | ERROR | KILLED}<br/>
 * {COMPLETED | ERROR | KILLED} => PURGED<br/>
 * UNKOWN : this is a bug if this status occurs<br/>
 */
public interface ExecutionProcess {
	/** The process is not yet usable. */
	public final static String NEW = "NEW";
	/** The process is usable but not yet scheduled to run. */
	public final static String INITIALIZED = "INITIALIZED";
	/** The process is usable and scheduled to run, but not yet running. */
	public final static String SCHEDULED = "SCHEDULED";
	/** The process is currently running. */
	public final static String RUNNING = "RUNNING";
	/** The process has properly completed. */
	public final static String COMPLETED = "COMPLETED";
	/** The process failed because of an unexpected error. */
	public final static String ERROR = "ERROR";
	/** The process was killed explicitly or through a crash. */
	public final static String KILLED = "KILLED";
	/** The status cannot be retrieved (probably because of unexpected errors). */
	public final static String UNKOWN = "UNKOWN";

	/**
	 * Only a reference to the process has been kept, all monitoring data such
	 * as logs have been purged.
	 */
	public final static String PURGED = "PURGED";

	/** The UUID of this process. */
	public String getUuid();

	/** The current status of this process. */
	public String getStatus();

	/** Sets the current status of this process */
	public void setStatus(String status);
}
