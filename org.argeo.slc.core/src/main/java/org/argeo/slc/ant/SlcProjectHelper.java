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
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.helper.ProjectHelper2;

import org.argeo.slc.core.structure.DefaultSRegistry;
import org.argeo.slc.core.structure.SimpleSElement;
import org.argeo.slc.core.structure.StructureRegistry;
import org.argeo.slc.core.structure.tree.TreeSPath;

/**
 * Custom implementation of an Ant <code>ProjectHelper</code> binding a Spring
 * application context and a structure registry with the Ant project.
 */
public class SlcProjectHelper extends ProjectHelper2 {
	private static Log log;

	/** The Ant reference to the Spring application context used. */
	public static String REF_ROOT_CONTEXT = "slcApplicationContext";
	/** The Ant reference to the SLC structure registry used. */
	public static String REF_STRUCTURE_REGISTRY = "slcStructureRegistry";
	/** The Ant reference to the <code>TreePath</code> of the current project */
	public static String REF_PROJECT_PATH = "slcProjectPath";
	/**
	 * Resource path to the property file listing the SLC specific Ant tasks:
	 * /org/argeo/slc/ant/taskdefs.properties
	 */
	private static String SLC_TASKDEFS_RESOURCE_PATH = "/org/argeo/slc/ant/taskdefs.properties";
	private static String SLC_TYPEDEFS_RESOURCE_PATH = "/org/argeo/slc/ant/typedefs.properties";

	protected SlcAntConfig slcAntConfig = null;

	@Override
	public void parse(Project project, Object source) throws BuildException {

		if (source instanceof File) {
			File sourceFile = (File) source;
			// Reset basedir property, in order to avoid base dir override when
			// running in Maven
			project.setProperty("basedir", sourceFile.getParentFile()
					.getAbsolutePath());
		}

		if (slcAntConfig != null) {
			// Config already initialized (probably import), only parse
			super.parse(project, source);
			return;
		}

		// Initialize config
		slcAntConfig = new SlcAntConfig();

		if (!slcAntConfig.initProject(project)) {
			// not SLC compatible, do normal Ant
			super.parse(project, source);
			return;
		}

		if (log == null) {
			// log4j is initialized only now
			log = LogFactory.getLog(SlcProjectHelper.class);
		}

		if (log.isDebugEnabled())
			log.debug("SLC properties are set, starting initialization for "
					+ source + " (projectHelper=" + this + ")");

		beforeParsing(project);

		// Calls the underlying implementation to do the actual work
		super.parse(project, source);

		afterParsing(project);
	}

	/**
	 * Performs operations after config initialization and before Ant file
	 * parsing. Performed only once when the main project file is parsed. Should
	 * be called by overriding methods.
	 */
	protected void beforeParsing(Project project) {
		// Init Spring application context
		initSpringContext(project);

		// Init structure registry
		DefaultSRegistry registry = new DefaultSRegistry();
		project.addReference(REF_STRUCTURE_REGISTRY, registry);
	}

	/**
	 * Performs operations after parsing of the main file. Called only once (not
	 * for imports).
	 */
	protected void afterParsing(Project project) {
		// Creates structure root
		registerProjectAndParents(project, slcAntConfig);
		addCustomTaskAndTypes(project);
	}

	/** Creates the tree-based structure for this project. */
	private void registerProjectAndParents(Project project,
			SlcAntConfig slcAntConfig) {
		StructureRegistry<TreeSPath> registry = (StructureRegistry<TreeSPath>) project
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
				description = project.getDescription() != null
						&& !project.getDescription().equals("") ? project
						.getDescription() : project.getName() != null ? project
						.getName() : slcAntConfig.getDescriptionForDir(dir);
			} else {
				description = slcAntConfig.getDescriptionForDir(dir);
				if (log.isTraceEnabled())
					log.trace("Dir desc " + i + "/" + dirs.size() + ": "
							+ description);
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
	private static TreeSPath getProjectPath(Project project) {
		return (TreeSPath) project.getReference(REF_PROJECT_PATH);
	}

	/** Initializes the Spring application context. */
	private void initSpringContext(Project project) {
		System.getProperties().putAll((Map<?, ?>) project.getProperties());
		String acPath = project
				.getUserProperty(SlcAntConfig.APPLICATION_CONTEXT_PROPERTY);
		if (log.isDebugEnabled())
			log.debug("Loading Spring application context from " + acPath);
		// FIXME: workaround to the removal of leading '/' by Spring
		// use URL instead?
		AbstractApplicationContext context = new FileSystemXmlApplicationContext(
				'/' + acPath);
		context.registerShutdownHook();
		project.addReference(REF_ROOT_CONTEXT, context);

		// Add build listeners declared in Spring context
		Map<String, BuildListener> listeners = context.getBeansOfType(
				BuildListener.class, false, true);
		for(BuildListener listener: listeners.values()){
			project.addBuildListener(listener);
		}
	}

	/** Loads the SLC specific Ant tasks. */
	protected static void addCustomTaskAndTypes(Project project) {
		Properties taskdefs = getDefs(project, SLC_TASKDEFS_RESOURCE_PATH);
		for (Object o : taskdefs.keySet()) {
			String name = o.toString();
			try {
				project.addTaskDefinition(name, Class.forName(taskdefs
						.getProperty(name)));
			} catch (ClassNotFoundException e) {
				log.error("Unknown class for task " + name, e);
			}
		}
		Properties typedefs = getDefs(project, SLC_TYPEDEFS_RESOURCE_PATH);
		for (Object o : typedefs.keySet()) {
			String name = o.toString();
			try {
				project.addDataTypeDefinition(name, Class.forName(typedefs
						.getProperty(name)));
			} catch (ClassNotFoundException e) {
				log.error("Unknown class for type " + name, e);
			}
		}
	}

	private static Properties getDefs(Project project, String path) {
		Properties defs = new Properties();
		try {
			InputStream in = project.getClass().getResourceAsStream(path);
			defs.load(in);
			in.close();
		} catch (IOException e) {
			throw new SlcAntException("Cannot load task definitions", e);
		}
		return defs;
	}
}
