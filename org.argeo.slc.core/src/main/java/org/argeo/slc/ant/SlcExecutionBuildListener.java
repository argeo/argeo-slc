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
import org.argeo.slc.core.process.WebServiceSlcExecutionNotifier;

public class SlcExecutionBuildListener extends AppenderSkeleton implements
		ProjectRelatedBuildListener {
	public static final String ANT_TYPE = "org.apache.tools.ant";
	public static final String SLC_ANT_TYPE = "org.argeo.slc.ant";

	public static final String REF_SLC_EXECUTION = "slcExecution";

	private Project project;

	// to avoid stack overflow when logging for log4j
	private boolean isLogging = false;

	private List<SlcExecutionNotifier> notifiers = new Vector<SlcExecutionNotifier>();

	// private Map<SlcExecution, SlcExecutionStep> currentStep = new
	// HashMap<SlcExecution, SlcExecutionStep>();

	// private SlcExecutionStep currentStep = null;
	private boolean currentStepNotified = true;

	public void init(Project project) {
		if (this.project != null) {
			throw new SlcAntException("Build listener already initialized");
		}

		this.project = project;

		if (!LogManager.getRootLogger().isAttached(this)) {
			LogManager.getRootLogger().addAppender(this);
		}

		SlcExecution slcExecution = (SlcExecution) project
				.getReference(REF_SLC_EXECUTION);
		if (slcExecution == null)
			throw new SlcAntException("No SLC Execution registered.");

		for (SlcExecutionNotifier notifier : notifiers) {
			notifier.newExecution(slcExecution);
		}

	}

	public void buildStarted(BuildEvent event) {
		// SlcExecution slcExecution = getSlcExecution(event);

	}

	public void buildFinished(BuildEvent event) {
		SlcExecution slcExecution = getSlcExecution(event);
		slcExecution.setStatus(SlcExecution.STATUS_FINISHED);

		for (SlcExecutionNotifier notifier : notifiers) {
			notifier.updateExecution(slcExecution);
		}
	}

	public void messageLogged(BuildEvent event) {
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
		addLogStep(event, "Target " + event.getTarget().getName() + " started");
	}

	public void targetFinished(BuildEvent event) {
		addLogStep(event, "Target " + event.getTarget().getName() + " finished");
	}

	public void taskStarted(BuildEvent event) {
		SlcExecution slcExecution = getSlcExecution(event);
		if (!currentStepNotified) {
			notifyStep(slcExecution, slcExecution.currentStep());
			currentStepNotified = true;
		}

		slcExecution.getSteps().add(
				new SlcExecutionStep("LOG", "Task "
						+ event.getTask().getTaskName() + " started"));
		currentStepNotified = false;
	}

	public void taskFinished(BuildEvent event) {
		SlcExecution slcExecution = getSlcExecution(event);
		if (!currentStepNotified) {
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
				.getReference(REF_SLC_EXECUTION);

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

		if (event.getLoggerName().equals(
				WebServiceSlcExecutionNotifier.class.getName())) {
			return;
		}

		isLogging = true;

		try {
			SlcExecution slcExecution = (SlcExecution) project
					.getReference(REF_SLC_EXECUTION);
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

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean requiresLayout() {
		// TODO Auto-generated method stub
		return false;
	}

	public Project getProject() {
		return project;
	}

}
