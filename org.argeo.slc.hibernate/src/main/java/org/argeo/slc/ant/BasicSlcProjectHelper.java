package org.argeo.slc.ant;

import org.springframework.context.ApplicationContext;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.helper.ProjectHelper2;

import org.argeo.slc.core.structure.SimpleSElement;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.structure.tree.TreeSRegistry;

public class BasicSlcProjectHelper extends ProjectHelper2 {
	private ApplicationContext context;

	private String projectRootPath = "/project";

	@Override
	public void parse(Project project, Object source) throws BuildException {
		TreeSRegistry registry = new TreeSRegistry();
		TreeSPath projectPath = TreeSPath.parseToCreatePath(projectRootPath);
		
		// FIXME
		registry.register(projectPath, new SimpleSElement("ROOT"));
		
		project.addReference(SlcProjectHelper.REF_STRUCTURE_REGISTRY, registry);
		project.addReference(SlcProjectHelper.REF_PROJECT_PATH, projectPath);

		super.parse(project, source);
		
		project.addReference(SlcProjectHelper.REF_ROOT_CONTEXT, context);
		SlcProjectHelper.createAndRegisterSlcExecution(project);

		SlcProjectHelper.addCustomTaskAndTypes(project);
	}

	public void setContext(ApplicationContext context) {
		this.context = context;
	}

	
	
}
