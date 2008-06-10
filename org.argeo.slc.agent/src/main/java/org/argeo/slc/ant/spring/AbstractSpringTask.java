package org.argeo.slc.ant.spring;

import org.springframework.context.ApplicationContext;

import org.apache.tools.ant.Task;

import org.argeo.slc.ant.SlcAntConstants;
import org.argeo.slc.core.process.SlcExecution;

/** Abstract Ant task providing access to a Spring context. */
public abstract class AbstractSpringTask extends Task {

	/** Gets the related Spring context. */
	protected ApplicationContext getContext() {
		return (ApplicationContext) getProject().getReference(
				SlcAntConstants.REF_ROOT_CONTEXT);
	}

	/** Gets the related slc execution or null if not is registered. */
	protected SlcExecution getSlcExecution() {
		return (SlcExecution) getProject().getReference(
				SlcAntConstants.REF_SLC_EXECUTION);
	}
}
