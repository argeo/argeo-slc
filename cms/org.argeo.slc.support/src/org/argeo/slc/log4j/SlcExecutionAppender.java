package org.argeo.slc.log4j;

import java.util.Date;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.argeo.slc.execution.ExecutionStep;
import org.argeo.slc.runtime.ExecutionThread;
import org.argeo.slc.runtime.ProcessThreadGroup;

/** Not meant to be used directly in standard log4j config */
public class SlcExecutionAppender extends AppenderSkeleton {

	private Boolean disabled = false;

	private String level = null;

	private Level log4jLevel = null;

	/** Marker to prevent stack overflow */
	private ThreadLocal<Boolean> dispatching = new ThreadLocal<Boolean>() {

		@Override
		protected Boolean initialValue() {
			return false;
		}
	};

	// private Layout layout = null;
	// private String pattern = "%m - %c%n";
	private Boolean onlyExecutionThread = false;

	public void init() {
		// if (layout != null)
		// setLayout(layout);
		// else
		// setLayout(new PatternLayout(pattern));
		Logger.getRootLogger().addAppender(this);
	}

	@Override
	protected void append(LoggingEvent event) {
		if (disabled)
			return;

		if (dispatching.get())
			return;

		if (level != null && !level.trim().equals("")) {
			if (log4jLevel == null || !log4jLevel.toString().equals(level))
				try {
					log4jLevel = Level.toLevel(level);
				} catch (Exception e) {
					System.err.println("Log4j level could not be set for level '" + level + "', resetting it to null.");
					e.printStackTrace();
					level = null;
				}

			if (log4jLevel != null && !event.getLevel().isGreaterOrEqual(log4jLevel)) {
				return;
			}
		}

		// Check whether we are within an executing process
		Thread currentThread = Thread.currentThread();
		if (currentThread.getThreadGroup() instanceof ProcessThreadGroup) {
			if (onlyExecutionThread && !(currentThread instanceof ExecutionThread))
				return;

			final String type;
			if (event.getLevel().equals(Level.ERROR) || event.getLevel().equals(Level.FATAL))
				type = ExecutionStep.ERROR;
			else if (event.getLevel().equals(Level.WARN))
				type = ExecutionStep.WARNING;
			else if (event.getLevel().equals(Level.INFO))
				type = ExecutionStep.INFO;
			else if (event.getLevel().equals(Level.DEBUG))
				type = ExecutionStep.DEBUG;
			else if (event.getLevel().equals(Level.TRACE))
				type = ExecutionStep.TRACE;
			else
				type = ExecutionStep.INFO;

			ExecutionStep step = new ExecutionStep(event.getLoggerName(), new Date(event.getTimeStamp()), type,
					event.getMessage().toString());

			try {
				dispatching.set(true);
				BlockingQueue<ExecutionStep> steps = ((ProcessThreadGroup) currentThread.getThreadGroup()).getSteps();
				if (steps.remainingCapacity() == 0) {
					stdOut("WARNING: execution steps queue is full, skipping step: " + step);
					// FIXME understand why it block indefinitely: the queue
					// should be emptied by the logging thread
				} else {
					steps.add(step);
				}
			} finally {
				dispatching.set(false);
			}
		}
	}

	public void destroy() throws Exception {
		Logger.getRootLogger().removeAppender(this);
	}

	public void close() {
	}

	public boolean requiresLayout() {
		return false;
	}

	// public void setLayout(Layout layout) {
	// this.layout = layout;
	// }

	/** For development purpose, since using regular logging is not easy here */
	static void stdOut(Object obj) {
		System.out.println(obj);
	}

	// public void setPattern(String pattern) {
	// this.pattern = pattern;
	// }

	public void setOnlyExecutionThread(Boolean onlyExecutionThread) {
		this.onlyExecutionThread = onlyExecutionThread;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}

	public void setLevel(String level) {
		this.level = level;
	}

}
