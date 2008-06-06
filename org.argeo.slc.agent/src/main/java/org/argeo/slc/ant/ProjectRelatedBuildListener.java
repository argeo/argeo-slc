package org.argeo.slc.ant;

import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;

public interface ProjectRelatedBuildListener extends BuildListener {
	public Project getProject();

	/** SlcExecution must already have been registered in project */
	public void init(Project project);
}
