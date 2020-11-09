package org.argeo.slc.execution;

import java.io.Serializable;
import java.util.Date;

/**
 * An atomic step to be notified in during an {@link ExecutionProcess}. Can be a
 * log or the start/end of a phase, etc.
 */
public class ExecutionStep implements Serializable {
	private static final long serialVersionUID = 798640526532912161L;

	public final static String PHASE_START = "PHASE_START";
	public final static String PHASE_END = "PHASE_END";
	public final static String ERROR = "ERROR";
	public final static String WARNING = "WARNING";
	public final static String INFO = "INFO";
	public final static String DEBUG = "DEBUG";
	public final static String TRACE = "TRACE";

	/** @deprecated */
	public final static String START = "START";
	/** @deprecated */
	public final static String END = "END";

	// TODO make the fields final and private when we don't need POJO support
	// anymore (that
	// is when SlcExecutionStep is removed)
	protected String type;
	protected String thread;
	protected Date timestamp;
	protected String log;

	private String location;

	/** Empty constructor */
	public ExecutionStep() {
		Thread currentThread = Thread.currentThread();
		thread = currentThread.getName();
	}

	/** Creates a step at the current date */
	public ExecutionStep(String location, String type, String log) {
		this(location, new Date(), type, log);
	}

	/** Creates a step of the given type. */
	public ExecutionStep(String location, Date timestamp, String type,
			String log) {
		this(location, timestamp, type, log, Thread.currentThread().getName());
	}

	public ExecutionStep(String location, Date timestamp, String type,
			String log, String thread) {
		this.location = location;
		this.type = type;
		this.timestamp = timestamp;
		this.thread = thread;
		this.log = addLog(log);
	}

	public String getType() {
		return type;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public String getThread() {
		return thread;
	}

	/**
	 * Return the string that should be stored in the log field. Can be null if
	 * another mechanism is used to store log lines.
	 */
	protected String addLog(String log) {
		return log;
	}

	public String getLog() {
		return log;
	}

	@Override
	public String toString() {
		return "Execution step, thread=" + thread + ", type=" + type;
	}

	/** Typically the logging category */
	public String getLocation() {
		return location;
	}

}
