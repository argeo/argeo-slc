package org.argeo.slc.ant.spring;

import org.springframework.context.ApplicationContext;

import org.apache.tools.ant.Task;

import org.argeo.slc.ant.SlcProjectHelper;

public abstract class AbstractSpringTask extends Task {

	protected ApplicationContext getContext() {
		return (ApplicationContext) getProject().getReference(
				SlcProjectHelper.REF_ROOT_CONTEXT);
	}

}
