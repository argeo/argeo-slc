package org.argeo.slc.ant;

import org.apache.tools.ant.Project;
import org.argeo.slc.core.process.SlcExecution;
import org.argeo.slc.runtime.SlcExecutionContext;
import org.springframework.context.ApplicationContext;

public class AntExecutionContext implements SlcExecutionContext {
	private final Project project;

	public AntExecutionContext(Project project) {
		this.project = project;
	}

	public Object getBean(String name) {
		ApplicationContext context = (ApplicationContext) project
				.getReference(SlcAntConstants.REF_ROOT_CONTEXT);
		return context.getBean(name);
	}

	public SlcExecution getSlcExecution() {
		return (SlcExecution) project
				.getReference(SlcAntConstants.REF_SLC_EXECUTION);
	}

	public Project getProject() {
		return project;
	}
}
