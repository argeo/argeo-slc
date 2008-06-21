package org.argeo.slc.ant;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.MDC;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;
import org.argeo.slc.core.SlcException;
import org.argeo.slc.core.process.SlcExecution;
import org.argeo.slc.core.process.SlcExecutionNotifier;
import org.argeo.slc.core.process.SlcExecutionStep;

public class SlcExecutionBuildListener extends AppenderSkeleton implements
		BuildListener {
	private List<SlcExecutionNotifier> notifiers = new Vector<SlcExecutionNotifier>();

	private boolean currentStepNotified = true;

	// CUSTOMIZATIONS
	/**
	 * Whether to log Ant initialization stuff before the first target has been
	 * called.
	 */
	private boolean logBeforeFirstTarget = false;
	/** Whether the first target has been called. */
	private boolean firstTargetStarted = false;

	private boolean logTaskStartFinish = true;

	public SlcExecutionBuildListener() {
		// Default log level
		setThreshold(Level.INFO);
	}

	public void buildStarted(BuildEvent event) {
		SlcExecution slcExecution = getSlcExecution(event);
		for (SlcExecutionNotifier notifier : notifiers) {
			notifier.newExecution(slcExecution);
		}
	}

	public void buildFinished(BuildEvent event) {
		SlcExecution slcExecution = getSlcExecution(event);
		String oldStatus = slcExecution.getStatus();
		slcExecution.setStatus(SlcExecution.STATUS_FINISHED);

		for (SlcExecutionNotifier notifier : notifiers) {
			notifier.updateStatus(slcExecution, oldStatus, slcExecution
					.getStatus());
		}
	}

	public void messageLogged(BuildEvent event) {
		if (!shouldLog())
			return;

		SlcExecution slcExecution = getSlcExecution(event);
		if (slcExecution != null) {
			if (currentStepNotified) {
				slcExecution.getSteps().add(
						new SlcExecutionStep("LOG", event.getMessage()));
				notifyStep(slcExecution, slcExecution.currentStep());
				currentStepNotified = true;
			} else {
				slcExecution.currentStep().addLog(event.getMessage());
			}
		} else {
			// TODO: log before initialization?
		}
	}

	public void targetStarted(BuildEvent event) {
		if (!firstTargetStarted)
			firstTargetStarted = true;

		addLogStep(event, "Target " + event.getTarget().getName() + " started");
	}

	public void targetFinished(BuildEvent event) {
		addLogStep(event, "Target " + event.getTarget().getName() + " finished");
	}

	public void taskStarted(BuildEvent event) {
		if (!shouldLog())
			return;

		SlcExecution slcExecution = getSlcExecution(event);
		if (!currentStepNotified) {
			notifyStep(slcExecution, slcExecution.currentStep());
			currentStepNotified = true;
		}

		String msg = null;
		if (logTaskStartFinish)
			msg = "Task " + event.getTask().getTaskName() + " started";

		slcExecution.getSteps().add(new SlcExecutionStep("LOG", msg));

		currentStepNotified = false;
	}

	public void taskFinished(BuildEvent event) {
		if (!shouldLog())
			return;

		SlcExecution slcExecution = getSlcExecution(event);
		if (!currentStepNotified) {

			if (logTaskStartFinish)
				slcExecution.currentStep().addLog(
						"Task " + event.getTask().getTaskName() + " finished");

			notifyStep(slcExecution, slcExecution.currentStep());
			currentStepNotified = true;
		}
	}

	public void setNotifiers(List<SlcExecutionNotifier> notifiers) {
		this.notifiers = notifiers;
	}

	protected SlcExecution getSlcExecution(BuildEvent event) {
		return getSlcExecution(event.getProject());
	}

	protected SlcExecution getSlcExecution(Project project) {
		SlcExecution slcExecution = (SlcExecution) project
				.getReference(SlcAntConstants.REF_SLC_EXECUTION);

		if (slcExecution == null)
			throw new SlcException("No SLC Execution registered.");
		return slcExecution;
	}

	protected void addLogStep(BuildEvent event, String msg) {
		SlcExecution slcExecution = getSlcExecution(event);
		slcExecution.getSteps().add(new SlcExecutionStep("LOG", msg));

		notifyStep(slcExecution, slcExecution.currentStep());
		currentStepNotified = true;
	}

	protected void notifyStep(SlcExecution slcExecution, SlcExecutionStep step) {
		Vector<SlcExecutionStep> additionalSteps = new Vector<SlcExecutionStep>();
		additionalSteps.add(step);
		notifySteps(slcExecution, additionalSteps);
	}

	protected void notifySteps(SlcExecution slcExecution,
			List<SlcExecutionStep> additionalSteps) {
		for (SlcExecutionNotifier notifier : notifiers) {
			notifier.addSteps(slcExecution, additionalSteps);
		}
	}

	/* Log4j methods */

	@Override
	protected void append(LoggingEvent event) {
		Project project = (Project) MDC.get(SlcAntConstants.MDC_ANT_PROJECT);
		if (project == null)
			throw new SlcException("No Ant project registered in Log4j MDC.");

		SlcExecution slcExecution = getSlcExecution(project);
		if (currentStepNotified) {
			slcExecution.getSteps().add(
					new SlcExecutionStep("LOG", event.getMessage().toString()));
			currentStepNotified = false;
		} else {
			slcExecution.currentStep().addLog(event.getMessage().toString());
		}
	}

	protected boolean shouldLog() {
		return logBeforeFirstTarget || firstTargetStarted;
	}

	@Override
	public void close() {
	}

	@Override
	public boolean requiresLayout() {
		return false;
	}

	public void setLogBeforeFirstTarget(boolean logBeforeFirstTarget) {
		this.logBeforeFirstTarget = logBeforeFirstTarget;
	}

	public void setLogTaskStartFinish(boolean logTaskStartFinish) {
		this.logTaskStartFinish = logTaskStartFinish;
	}

	public void setLogLevel(String logLevel) {
		setThreshold(Level.toLevel(logLevel));
	}

}
