package org.argeo.slc.ant;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.helper.ProjectHelperImpl;

import org.argeo.slc.core.structure.DefaultSRegistry;
import org.argeo.slc.core.structure.tree.TreeSElement;
import org.argeo.slc.core.structure.tree.TreeSPath;

/**
 * Custom implementation of a <code>ProjectHelper</code> binding a Spring
 * application context and a structure registry with the Ant project.
 */
public class SlcProjectHelper extends ProjectHelperImpl {
	private static Log log = LogFactory.getLog(SlcProjectHelper.class);

	public static String REF_ROOT_CONTEXT = "slcApplicationContext";
	public static String REF_STRUCTURE_REGISTRY = "slcStructureRegistry";

	private String slcRootFileName = "slcRoot.properties";

	@Override
	public void parse(Project project, Object source) throws BuildException {
		log.debug("Entered SLC project helper");

		// look for root file
		File projectBaseDir = project.getBaseDir();
		File slcRootFile = findSlcRootFile(projectBaseDir);
		if (slcRootFile == null) {
			throw new SlcAntException("Cannot find SLC root file");
		}
		SlcAntConfig.initProject(project, slcRootFile);

		// init Spring application context
		String acPath = project
				.getUserProperty(SlcAntConfig.APPLICATION_CONTEXT_PROPERTY);
		ApplicationContext context = new FileSystemXmlApplicationContext(acPath);
		project.addReference(REF_ROOT_CONTEXT, context);

		// init structure register if it does not exist
		DefaultSRegistry registry = new DefaultSRegistry();
		project.addReference(REF_STRUCTURE_REGISTRY, registry);

		// call the underlying implementation to do the actual work
		super.parse(project, source);

		addSlcTasks(project);

		// create structure root
		TreeSElement element = new TreeSElement(project.getDescription(),
				"Root");
		registry.register(getProjectPath(project), element);

	}

	/** Get the path of a project (root). */
	public static TreeSPath getProjectPath(Project project) {
		return TreeSPath.createRootPath(getProjectPathName(project));
	}

	private static String getProjectPathName(Project project) {
		String projectName = project.getName() != null ? project.getName()
				: "project";
		return projectName;
	}

	private File findSlcRootFile(File dir) {
		for (File file : dir.listFiles()) {
			if (!file.isDirectory() && file.getName().equals(slcRootFileName)) {
				return file;
			}
		}

		File parentDir = dir.getParentFile();
		if (parentDir == null) {
			return null;// stop condition: not found
		} else {
			return findSlcRootFile(parentDir);
		}
	}

	private void addSlcTasks(Project project) {
		Properties taskdefs = new Properties();
		try {
			InputStream in = project.getClass().getResourceAsStream(
					"/org/argeo/slc/ant/taskdefs.properties");
			taskdefs.load(in);
			in.close();
		} catch (IOException e) {
			throw new SlcAntException("Cannot load task definitions", e);
		}

		for (Object o : taskdefs.keySet()) {
			String name = o.toString();
			try {
				project.addTaskDefinition(name, Class.forName(taskdefs
						.getProperty(name)));
			} catch (ClassNotFoundException e) {
				log.error("Unknown class for task " + name, e);
			}
		}
	}
}
