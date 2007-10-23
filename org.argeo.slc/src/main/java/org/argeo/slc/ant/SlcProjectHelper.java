package org.argeo.slc.ant;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.helper.ProjectHelperImpl;

import org.argeo.slc.core.structure.tree.TreeSElement;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.structure.tree.TreeSRegistry;

public class SlcProjectHelper extends ProjectHelperImpl {
	public static String PROP_APPLICATION_CONTEXT = "org.argeo.slc.slcRootContext";
	//public static String PROP_REGISTRY_MODE = "org.argeo.slc.slcRegistryMode";
	public static String REF_ROOT_CONTEXT = "slcApplicationContext";
	public static String REF_STRUCTURE_REGISTRY = "slcStructureRegistry";

	@Override
	public void parse(Project project, Object source) throws BuildException {
		stdOut("Entered SLC project helper");

		// init Spring application context
		String acPath = System.getProperty(PROP_APPLICATION_CONTEXT,
				"applicationContext.xml");
		ApplicationContext context = new FileSystemXmlApplicationContext(acPath);
		project.addReference(REF_ROOT_CONTEXT, context);

		// init structure register if it does not exist
			TreeSRegistry registry = new TreeSRegistry();
			project.addReference(REF_STRUCTURE_REGISTRY, registry);

			// call the underlying implementation to do the actual work
			super.parse(project, source);

			String projectDescription = project.getDescription() != null ? project
					.getDescription()
					: "Root";
			TreeSElement element = TreeSElement.createRootElelment(
					getProjectPathName(project), projectDescription);
			registry.register(element);
	}

	private static void stdOut(Object o) {
		System.out.println(o);
	}

	static TreeSPath getProjectPath(Project project) {
		return TreeSPath.createChild(null, getProjectPathName(project));
	}

	private static String getProjectPathName(Project project) {
		String projectName = project.getName() != null ? project.getName()
				: "project";
		return projectName;
	}
}
