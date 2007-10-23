package org.argeo.slc.ant;

import org.springframework.context.ApplicationContext;

import org.apache.tools.ant.Task;

public abstract class AbstractSpringTask extends Task {

	protected ApplicationContext getContext() {
		return (ApplicationContext) getProject().getReference(
				SlcProjectHelper.REF_ROOT_CONTEXT);
	}

}
