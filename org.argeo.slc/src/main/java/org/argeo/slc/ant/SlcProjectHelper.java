package org.argeo.slc.ant;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.helper.ProjectHelperImpl;

import org.argeo.slc.core.structure.DefaultSRegistry;
import org.argeo.slc.core.structure.SimpleSElement;
import org.argeo.slc.core.structure.StructureRegistry;
import org.argeo.slc.core.structure.tree.TreeSPath;

/**
 * Custom implementation of an Ant <code>ProjectHelper</code> binding a Spring
 * application context and a structure registry with the Ant project.
 */
public class SlcProjectHelper extends ProjectHelperImpl {
	private static Log log;

	/** The Ant reference to the Spring application context used. */
	public static String REF_ROOT_CONTEXT = "slcApplicationContext";
	/** The Ant reference to the SLC structure registry used. */
	public static String REF_STRUCTURE_REGISTRY = "slcStructureRegistry";
	/** The Ant reference to the <code>TreePath</code> of the current project */
	private static String REF_PROJECT_PATH = "slcProjectPath";
	/**
	 * Resource path to the property file listing the SLC specific Ant tasks:
	 * /org/argeo/slc/ant/taskdefs.properties
	 */
	private static String SLC_TASKDEFS_RESOURCE_PATH = "/org/argeo/slc/ant/taskdefs.properties";

	@Override
	public void parse(Project project, Object source) throws BuildException {

		// initialize config
		SlcAntConfig slcAntConfig = new SlcAntConfig();
		
		if(!slcAntConfig.initProject(project)){
			// not SLC compatible, do normal Ant
			super.parse(project, source);
			return;
		}

		if (log == null) {
			// log4j is initialized only now
			log = LogFactory.getLog(SlcProjectHelper.class);
		}
		log.debug("SLC properties are set, starting initialization..");

		// init Spring application context
		initSpringContext(project);

		// init structure registry
		DefaultSRegistry registry = new DefaultSRegistry();
		project.addReference(REF_STRUCTURE_REGISTRY, registry);

		// call the underlying implementation to do the actual work
		super.parse(project, source);

		// create structure root
		registerProjectAndParents(project, slcAntConfig);

		addSlcTasks(project);

	}

	/** Creates the tree-based structure for this project. */
	private void registerProjectAndParents(Project project,
			SlcAntConfig slcAntConfig) {
		StructureRegistry registry = (StructureRegistry) project
				.getReference(REF_STRUCTURE_REGISTRY);
		File rootDir = new File(project
				.getUserProperty(SlcAntConfig.ROOT_DIR_PROPERTY))
				.getAbsoluteFile();
		File baseDir = project.getBaseDir().getAbsoluteFile();

		List<File> dirs = new Vector<File>();
		File currentDir = baseDir;
		do {
			dirs.add(currentDir);
			currentDir = currentDir.getParentFile();
			if (log.isTraceEnabled())
				log.trace("List " + currentDir);
		} while (!currentDir.equals(rootDir.getParentFile()));

		// first path is root dir (because of previous algorithm)
		TreeSPath currPath = TreeSPath.createRootPath(rootDir.getName());
		for (int i = dirs.size() - 1; i >= 0; i--) {
			File dir = dirs.get(i);

			// retrieves description for this path
			final String description;
			if (i == 0) {// project itself
				description = project.getDescription() != null ? project
						.getDescription() : "";
			} else {
				description = slcAntConfig.getDescriptionForDir(dir);
			}
			SimpleSElement element = new SimpleSElement(description);

			// creates and register path
			if (!dir.equals(rootDir)) {// already set
				currPath = currPath.createChild(dir.getName());
			}
			registry.register(currPath, element);
		}
		project.addReference(REF_PROJECT_PATH, currPath);
	}

	/** Gets the path of a project (root). */
	public static TreeSPath getProjectPath(Project project) {
		return (TreeSPath) project.getReference(REF_PROJECT_PATH);
	}

	/** Initializes the Spring application context. */
	private void initSpringContext(Project project) {
		System.getProperties().putAll((Map<?, ?>) project.getProperties());
		String acPath = project
				.getUserProperty(SlcAntConfig.APPLICATION_CONTEXT_PROPERTY);
		AbstractApplicationContext context = new FileSystemXmlApplicationContext(
				acPath);
		context.registerShutdownHook();
		project.addReference(REF_ROOT_CONTEXT, context);
	}

	/** Loads the SLC specific Ant tasks. */
	private void addSlcTasks(Project project) {
		Properties taskdefs = new Properties();
		try {
			InputStream in = project.getClass().getResourceAsStream(
					SLC_TASKDEFS_RESOURCE_PATH);
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
