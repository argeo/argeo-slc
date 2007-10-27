package org.argeo.slc.ant;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.helper.ProjectHelperImpl;

import org.argeo.slc.core.structure.DefaultSRegistry;
import org.argeo.slc.core.structure.StructureRegistry;
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
	public static String REF_PROJECT_PATH = "slcProjectPath";

	private String slcRootFileName = "slcRoot.properties";
	private String slcLocalFileName = "slcLocal.properties";

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
		registerProjectAndParents(project);

		// TreeSElement element = new TreeSElement(project.getDescription(),
		// "Root");
		// registry.register(getProjectPath(project), element);

	}

	private void registerProjectAndParents(Project project) {
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
			log.trace("List " + currentDir);
		} while (!currentDir.equals(rootDir.getParentFile()));

		TreeSPath currPath = null;
		for (int i = dirs.size() - 1; i >= 0; i--) {
			File dir = dirs.get(i);

			String description = dir.getName();
			File slcLocal = new File(dir.getPath() + File.separator
					+ slcLocalFileName);
			if (slcLocal.exists()) {
				Properties properties = SlcAntConfig.loadFile(slcLocal
						.getAbsolutePath());
				description = properties
						.getProperty(SlcAntConfig.DIR_DESCRIPTION_PROPERTY);
			}
			TreeSElement element = new TreeSElement(description);

			if (dir.equals(rootDir)) {
				currPath = TreeSPath.createRootPath(rootDir.getName());
			} else {
				currPath = currPath.createChild(dir.getName());
			}
			registry.register(currPath, element);
		}
		project.addReference(REF_PROJECT_PATH, currPath);
	}

	/** Get the path of a project (root). */
	public static TreeSPath getProjectPath(Project project) {
		// return TreeSPath.createRootPath(getProjectPathName(project));
		return (TreeSPath) project.getReference(REF_PROJECT_PATH);
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
