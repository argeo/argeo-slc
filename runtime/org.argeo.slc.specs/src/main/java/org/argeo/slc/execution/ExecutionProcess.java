/*
 * Copyright (C) 2007-2012 Mathieu Baudier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.argeo.slc.execution;

import java.util.List;

/**
 * A process is the functional representation of a combination of executions.
 * While an execution is the actual java code running, a process exists before,
 * during and after the execution actually took place, providing an entry point
 * for the definition of executions, their monitoring (e.g. logging) and
 * tracking. A process can be distributed or parallelized. <br/>
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

	public void addSteps(List<ExecutionStep> steps);
}
