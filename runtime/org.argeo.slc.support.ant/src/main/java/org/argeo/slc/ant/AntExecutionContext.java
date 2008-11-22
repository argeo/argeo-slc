package org.argeo.slc.ant;

import org.apache.tools.ant.Project;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.runtime.SlcExecutionContext;
import org.springframework.context.ApplicationContext;

public class AntExecutionContext implements SlcExecutionContext {
	private final Project project;

	public AntExecutionContext(Project project) {
		this.project = project;
	}

	public <T> T getBean(String name) {
		ApplicationContext context = (ApplicationContext) project
				.getReference(AntConstants.REF_ROOT_CONTEXT);
		return (T) context.getBean(name);
	}

	public <T> T getAntRef(String antId) {
		return (T) project.getReference(antId);
	}

	public SlcExecution getSlcExecution() {
		return (SlcExecution) project
				.getReference(AntConstants.REF_SLC_EXECUTION);
	}

	public Project getProject() {
		return project;
	}
}
