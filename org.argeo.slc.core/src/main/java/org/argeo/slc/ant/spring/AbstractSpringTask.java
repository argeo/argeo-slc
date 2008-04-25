package org.argeo.slc.ant.spring;

import org.springframework.context.ApplicationContext;

import org.apache.tools.ant.Task;

import org.argeo.slc.ant.SlcExecutionBuildListener;
import org.argeo.slc.ant.SlcProjectHelper;
import org.argeo.slc.core.process.SlcExecution;

/** Abstract Ant task providing access to a Spring context. */
public abstract class AbstractSpringTask extends Task {

	/** Gets the related Spring context. */
	protected ApplicationContext getContext() {
		return (ApplicationContext) getProject().getReference(
				SlcProjectHelper.REF_ROOT_CONTEXT);
	}

	/** Gets the related slc execution or null if not is registered. */
	protected SlcExecution getSlcExecution() {
		return (SlcExecution) getProject().getReference(
				SlcExecutionBuildListener.REF_SLC_EXECUTION);
	}
}
