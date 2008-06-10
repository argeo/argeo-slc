package org.argeo.slc.ant;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.LogManager;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.Project;
import org.argeo.slc.core.process.SlcExecution;
import org.argeo.slc.core.process.SlcExecutionNotifier;
import org.argeo.slc.core.process.SlcExecutionStep;
import org.argeo.slc.ws.process.WebServiceSlcExecutionNotifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

public class SlcExecutionBuildListener extends AppenderSkeleton implements
		ProjectRelatedBuildListener {
	private Project project;

	// to avoid stack overflow when logging for log4j
	private boolean isLogging = false;

	private List<SlcExecutionNotifier> notifiers = new Vector<SlcExecutionNotifier>();

	private boolean currentStepNotified = true;

	// CUSTOMIZATIONS
	private boolean logBeforeFirstTarget = false;
	private boolean firstTargetStarted = false;

	private boolean logTaskStartFinish = true;

	public void init(Project project) {
		if (this.project != null) {
			throw new SlcAntException("Build listener already initialized");
		}

		this.project = project;

		if (!LogManager.getRootLogger().isAttached(this)) {
			LogManager.getRootLogger().addAppender(this);
		}

		SlcExecution slcExecution = (SlcExecution) project
				.getReference(SlcAntConstants.REF_SLC_EXECUTION);
		if (slcExecution == null)
			throw new SlcAntException("No SLC Execution registered.");

		for (SlcExecutionNotifier notifier : notifiers) {
			notifier.newExecution(slcExecution);
		}

	}

	public void buildStarted(BuildEvent event) {
	}

	public void buildFinished(BuildEvent event) {
		SlcExecution slcExecution = getSlcExecution(event);
		String oldStatus = slcExecution.getStatus();
		slcExecution.setStatus(SlcExecution.STATUS_FINISHED);

		for (SlcExecutionNotifier notifier : notifiers) {
			notifier.updateStatus(slcExecution, oldStatus, slcExecution
					.getStatus());
		}

//		AbstractApplicationContext context = (AbstractApplicationContext) getProject()
//				.getReference(SlcProjectHelper.REF_ROOT_CONTEXT);
//		if (context != null)
//			context.close();
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
		Project projectEvt = event.getProject();
		if (!projectEvt.equals(project))
			throw new SlcAntException("Event project " + projectEvt
					+ " not consistent with listener project " + project);

		SlcExecution slcExecution = (SlcExecution) project
				.getReference(SlcAntConstants.REF_SLC_EXECUTION);

		if (slcExecution == null)
			throw new SlcAntException("No SLC Execution registered.");
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
		if (isLogging) {
			// avoid StackOverflow if notification calls Log4j itself.
			return;
		}

		// FIXME: make it more generic
		if (event.getLoggerName().equals(
				WebServiceSlcExecutionNotifier.class.getName())) {
			return;
		}

		isLogging = true;

		try {
			SlcExecution slcExecution = (SlcExecution) project
					.getReference(SlcAntConstants.REF_SLC_EXECUTION);
			if (slcExecution != null) {
				if (currentStepNotified) {
					slcExecution.getSteps().add(
							new SlcExecutionStep("LOG", event.getMessage()
									.toString()));
					currentStepNotified = false;
				}
				slcExecution.currentStep()
						.addLog(event.getMessage().toString());
			} else {
				// TODO: log before initialization?
			}
		} finally {
			isLogging = false;
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

	public Project getProject() {
		return project;
	}

}
