package org.argeo.slc.ant;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.helper.ProjectHelper2;
import org.apache.tools.ant.listener.CommonsLoggingListener;
import org.argeo.slc.core.process.SlcExecution;
import org.argeo.slc.core.structure.DefaultSRegistry;
import org.argeo.slc.core.structure.SimpleSElement;
import org.argeo.slc.core.structure.StructureRegistry;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.structure.tree.TreeSRegistry;
import org.argeo.slc.runtime.SlcRuntime;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.Resource;

public class AntSlcApplication {
	private final static Log log = LogFactory.getLog(AntSlcApplication.class);

	private SlcRuntime slcRuntime;

	private Resource contextLocation;

	private Resource rootDir;
	private Resource confDir;
	private Resource workDir;

	public void execute(SlcExecution slcExecution, Properties properties,
			Map<String, Object> references) {
		if (log.isDebugEnabled()) {
			log.debug("### Start SLC execution " + slcExecution.getUuid()
					+ " ###");
			log.debug("rootDir=" + rootDir);
			log.debug("confDir=" + confDir);
			log.debug("workDir=" + workDir);
		}

		// Ant coordinates
		Resource script = findAntScript(slcExecution);
		List<String> targets = findAntTargets(slcExecution);

		ConfigurableApplicationContext ctx = createExecutionContext();

		Project project = new Project();
		project.addReference(SlcAntConstants.REF_ROOT_CONTEXT, ctx);
		project.addReference(SlcAntConstants.REF_SLC_EXECUTION, slcExecution);
		initProject(project, properties, references);
		parseProject(project, script);

		initStructure(project, script);
		runProject(project, targets);
	}

	protected Resource findAntScript(SlcExecution slcExecution) {
		String scriptStr = slcExecution.getAttributes().get(
				SlcAntConstants.EXECATTR_ANT_FILE);
		if (scriptStr == null)
			throw new SlcAntException("No Ant script provided");

		try {
			if (log.isTraceEnabled())
				log.trace("scriptStr=" + scriptStr);
			Resource script = null;

			if (rootDir != null) {
				script = rootDir.createRelative(scriptStr);
				if (log.isTraceEnabled())
					log.trace("script(relative)=" + script);
				if (script.exists())
					return script;
			}

			script = slcRuntime.getRuntimeContext().getResource(scriptStr);
			if (log.isTraceEnabled())
				log.trace("script(absolute)=" + script);
			if (script.exists())
				return script;

			throw new SlcAntException("Cannot find Ant script " + scriptStr);
		} catch (Exception e) {
			throw new SlcAntException("Cannot find Ant script " + scriptStr, e);
		}
	}

	protected List<String> findAntTargets(SlcExecution slcExecution) {
		String targetList = slcExecution.getAttributes().get(
				SlcAntConstants.EXECATTR_ANT_TARGETS);
		List<String> targets = new Vector<String>();
		if (targetList != null) {
			StringTokenizer stTargets = new StringTokenizer(targetList, ",");
			while (stTargets.hasMoreTokens()) {
				targets.add(stTargets.nextToken());
			}
		}
		return targets;
	}

	protected ConfigurableApplicationContext createExecutionContext() {
		try {
			if (confDir != null && contextLocation == null) {
				contextLocation = confDir
						.createRelative("applicationContext.xml");
			}

			GenericApplicationContext ctx = new GenericApplicationContext(
					slcRuntime.getRuntimeContext());
			if (contextLocation != null && contextLocation.exists()) {
				XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(
						ctx);
				xmlReader.loadBeanDefinitions(contextLocation);
			}
			return ctx;
		} catch (Exception e) {
			throw new SlcAntException(
					"Cannot create SLC execution application context.", e);
		}
	}

	protected void initProject(Project project, Properties properties,
			Map<String, Object> references) {
		if (properties != null) {
			for (Map.Entry<Object, Object> entry : properties.entrySet()) {
				project.setUserProperty(entry.getKey().toString(), entry
						.getValue().toString());
			}
		}

		if (references != null) {
			for (Map.Entry<String, Object> entry : references.entrySet()) {
				project.addReference(entry.getKey(), entry.getValue());
			}
		}

		project.addBuildListener(new CommonsLoggingListener());
		project.init();
		addCustomTaskAndTypes(project);
	}

	/** Loads the SLC specific Ant tasks. */
	protected void addCustomTaskAndTypes(Project project) {
		Properties taskdefs = getDefs(project,
				SlcAntConstants.SLC_TASKDEFS_RESOURCE_PATH);
		for (Object o : taskdefs.keySet()) {
			String name = o.toString();
			try {
				project.addTaskDefinition(name, Class.forName(taskdefs
						.getProperty(name)));
			} catch (ClassNotFoundException e) {
				log.error("Unknown class for task " + name, e);
			}
		}
		Properties typedefs = getDefs(project,
				SlcAntConstants.SLC_TYPEDEFS_RESOURCE_PATH);
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

	private Properties getDefs(Project project, String path) {
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

	protected void initStructure(Project project, Resource script) {
		// Init structure registry
		try {
			StructureRegistry<TreeSPath> registry = new TreeSRegistry();
			project.addReference(SlcAntConstants.REF_STRUCTURE_REGISTRY,
					registry);

			String scriptPath = script.getURL().getPath();
			if (rootDir != null) {
				scriptPath = scriptPath.substring(rootDir.getURL().getPath()
						.length());
				log.debug("rootDirPath=" + rootDir.getURL().getPath());
			}
			log.debug("scriptPath=" + scriptPath);

			List<String> dirNames = new Vector<String>();
			StringTokenizer st = new StringTokenizer(scriptPath, "/");
			TreeSPath currPath = null;
			while (st.hasMoreTokens()) {
				String name = st.nextToken();
				if (currPath == null) {
					currPath = TreeSPath.createRootPath(name);
				} else {
					currPath = currPath.createChild(name);
				}
				registry.register(currPath, new SimpleSElement(name));
			}
			TreeSPath projectPath = currPath
					.createChild(project.getName() != null
							&& !project.getName().equals("") ? project
							.getName() : "project");
			String projectDesc = project.getDescription() != null
					&& !project.getDescription().equals("") ? project
					.getDescription() : projectPath.getName();
			registry.register(projectPath, new SimpleSElement(projectDesc));
			project.addReference(SlcAntConstants.REF_PROJECT_PATH, currPath);

			if (log.isDebugEnabled())
				log.debug("Project path: " + projectPath);
		} catch (IOException e) {
			throw new SlcAntException("Cannot inititalize execution structure",
					e);
		}

		/*
		 * File rootDir = new File(project
		 * .getUserProperty(SlcAntConstants.ROOT_DIR_PROPERTY))
		 * .getAbsoluteFile(); File baseDir =
		 * project.getBaseDir().getAbsoluteFile(); List<File> dirs = new Vector<File>();
		 * File currentDir = baseDir; do { dirs.add(currentDir); currentDir =
		 * currentDir.getParentFile(); if (log.isTraceEnabled()) log.trace("List " +
		 * currentDir); } while (!currentDir.equals(rootDir.getParentFile())); //
		 * first path is root dir (because of previous algorithm) TreeSPath
		 * currPath = TreeSPath.createRootPath(rootDir.getName()); for (int i =
		 * dirs.size() - 1; i >= 0; i--) { File dir = dirs.get(i); // retrieves
		 * description for this path final String description; if (i == 0) {//
		 * project itself description = project.getDescription() != null &&
		 * !project.getDescription().equals("") ? project .getDescription() :
		 * project.getName(); } else { description = dir.getName(); if
		 * (log.isTraceEnabled()) log.trace("Dir desc " + i + "/" + dirs.size() + ": " +
		 * description); } SimpleSElement element = new
		 * SimpleSElement(description); // creates and register path if
		 * (!dir.equals(rootDir)) {// already set currPath =
		 * currPath.createChild(dir.getName()); } registry.register(currPath,
		 * element); } project.addReference(SlcAntConstants.REF_PROJECT_PATH,
		 * currPath);
		 */
	}

	protected void parseProject(Project project, Resource script) {
		try {
			File baseDir = null;
			try {
				File scriptFile = script.getFile();
				baseDir = scriptFile.getParentFile();
			} catch (IOException e) {// resource is not a file
				baseDir = new File(System.getProperty("user.dir"));
			}
			project.setBaseDir(baseDir);
			// Reset basedir property, in order to avoid base dir override when
			// running in Maven
			project.setProperty("basedir", baseDir.getAbsolutePath());

			ProjectHelper2 projectHelper = new ProjectHelper2();
			project.addReference(ProjectHelper.PROJECTHELPER_REFERENCE,
					projectHelper);
			projectHelper.parse(project, script.getURL());
		} catch (Exception e) {
			throw new SlcAntException("Could not parse project for script "
					+ script, e);
		}

	}

	protected void runProject(Project p, List<String> targets) {
		p.fireBuildStarted();
		Throwable exception = null;
		try {
			if (targets.size() == 0) {// no target defined
				p.executeTarget(p.getDefaultTarget());
			} else {
				p.executeTargets(new Vector<String>(targets));
			}
		} catch (Throwable e) {
			exception = e;
			throw new SlcAntException("SLC Ant execution failed", exception);
		} finally {
			p.fireBuildFinished(exception);
		}
	}

	public void setSlcRuntime(SlcRuntime slcRuntime) {
		this.slcRuntime = slcRuntime;
	}

	public void setContextLocation(Resource contextLocation) {
		this.contextLocation = contextLocation;
	}

	public void setRootDir(Resource rootDir) {
		this.rootDir = rootDir;
	}

	public void setConfDir(Resource confDir) {
		this.confDir = confDir;
	}

	public void setWorkDir(Resource workDir) {
		this.workDir = workDir;
	}

}
