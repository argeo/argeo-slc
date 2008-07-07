package org.argeo.slc.ant;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Appender;
import org.apache.log4j.LogManager;
import org.apache.log4j.MDC;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.helper.ProjectHelper2;
import org.apache.tools.ant.listener.CommonsLoggingListener;
import org.argeo.slc.core.SlcException;
import org.argeo.slc.core.process.SlcExecution;
import org.argeo.slc.core.structure.SimpleSElement;
import org.argeo.slc.core.structure.StructureRegistry;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.structure.tree.TreeSRegistry;
import org.argeo.slc.logging.Log4jUtils;
import org.argeo.slc.runtime.SlcExecutionOutput;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.SystemPropertyUtils;

public class AntSlcApplication {
	private final static String DEFAULT_APP_LOG4J_PROPERTIES = "org/argeo/slc/ant/defaultAppLog4j.properties";

	private final static Log log = LogFactory.getLog(AntSlcApplication.class);

	private Resource contextLocation;
	private ConfigurableApplicationContext parentContext;

	private Resource rootDir;
	private Resource confDir;
	private File workDir;

	public void execute(SlcExecution slcExecution, Properties properties,
			Map<String, Object> references,
			SlcExecutionOutput<AntExecutionContext> executionOutput) {

		// Properties and application logging initialization
		initSystemProperties(properties);
		Log4jUtils.initLog4j("classpath:" + DEFAULT_APP_LOG4J_PROPERTIES);

		log.info("\n###\n### Start SLC execution " + slcExecution.getUuid()
				+ "\n###\n");
		if (log.isDebugEnabled()) {
			log.debug("rootDir=" + rootDir);
			log.debug("confDir=" + confDir);
			log.debug("workDir=" + workDir);
		}

		// Ant coordinates
		String scriptRelativePath = findAntScript(slcExecution);
		List<String> targets = findAntTargets(slcExecution);

		// Spring initialization
		ConfigurableApplicationContext ctx = createExecutionContext(slcExecution);

		// Ant project initialization
		Project project = new Project();
		AntExecutionContext executionContext = new AntExecutionContext(project);
		project.addReference(AntConstants.REF_ROOT_CONTEXT, ctx);
		project.addReference(AntConstants.REF_SLC_EXECUTION, slcExecution);

		try {
			initProject(project, properties, references);
			parseProject(project, scriptRelativePath);

			// Execute project
			initStructure(project, scriptRelativePath);
			runProject(project, targets);

			if (executionOutput != null)
				executionOutput.postExecution(executionContext);
		} finally {
			ctx.close();
		}
	}

	protected void initSystemProperties(Properties userProperties) {
		// Set user properties as system properties so that Spring can access
		// them
		if (userProperties != null) {
			for (Object key : userProperties.keySet()) {
				System.setProperty(key.toString(), userProperties
						.getProperty(key.toString()));
			}
		}

		if (System.getProperty(AntConstants.DEFAULT_TEST_RUN_PROPERTY) == null) {
			System.setProperty(AntConstants.DEFAULT_TEST_RUN_PROPERTY,
					"defaultTestRun");
		}

		try {
			if (rootDir != null)
				setSystemPropertyForRes(AntConstants.ROOT_DIR_PROPERTY, rootDir);
			if (confDir != null)
				setSystemPropertyForRes(AntConstants.CONF_DIR_PROPERTY, confDir);
			if (workDir != null)
				System.setProperty(AntConstants.WORK_DIR_PROPERTY, workDir
						.getCanonicalPath());

			// Additional properties in slc.properties file. Already set sytem
			// properties (such as the various directories) can be resolved in
			// placeholders.
			if (confDir != null) {
				Resource slcPropertiesRes = confDir
						.createRelative("slc.properties");
				if (slcPropertiesRes.exists()) {
					Properties slcProperties = new Properties();
					InputStream in = slcPropertiesRes.getInputStream();
					try {
						slcProperties.load(in);
					} finally {
						IOUtils.closeQuietly(in);
					}

					for (String key : slcProperties.stringPropertyNames()) {
						if (!System.getProperties().containsKey(key)) {
							String value = SystemPropertyUtils
									.resolvePlaceholders(slcProperties
											.getProperty(key));
							System.setProperty(key, value);
						}
					}
				}
			}
		} catch (Exception e) {
			throw new SlcException("Cannot init system properties.", e);
		}
	}

	/**
	 * Set property as an absolute file path if the resource can be located on
	 * the file system, or as an url.
	 */
	private void setSystemPropertyForRes(String key, Resource res)
			throws IOException {
		String value = null;
		try {
			value = res.getFile().getCanonicalPath();
		} catch (IOException e) {
			value = res.getURL().toString();
		}
		System.setProperty(key, value);
	}

	protected ConfigurableApplicationContext createExecutionContext(
			SlcExecution slcExecution) {
		try {

			// Find runtime definition
			Resource runtimeRes = null;
			String runtimeStr = slcExecution.getAttributes().get(
					AntConstants.EXECATTR_RUNTIME);
			if (runtimeStr == null)
				runtimeStr = "default";

			ResourceLoader rl = new DefaultResourceLoader(getClass()
					.getClassLoader());
			try {// tries absolute reference
				runtimeRes = rl.getResource(runtimeStr);
			} catch (Exception e) {
				// silent
			}
			if (runtimeRes == null || !runtimeRes.exists()) {
				if (confDir != null)
					runtimeRes = confDir.createRelative("runtime/" + runtimeStr
							+ ".xml");
			}

			// Find runtime independent application context definition
			if (confDir != null && contextLocation == null) {
				contextLocation = confDir
						.createRelative("applicationContext.xml");
			}

			GenericApplicationContext ctx = new GenericApplicationContext(
					parentContext);
			ctx.setDisplayName("SLC Execution #" + slcExecution.getUuid());

			XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(ctx);
			if (runtimeRes != null && runtimeRes.exists())
				xmlReader.loadBeanDefinitions(runtimeRes);
			else
				log.warn("No runtime context defined");

			if (contextLocation != null && contextLocation.exists())
				xmlReader.loadBeanDefinitions(contextLocation);
			else
				log.warn("No runtime independent application context defined");

			// Add property place holder
			PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
			ppc.setIgnoreUnresolvablePlaceholders(true);
			ctx.addBeanFactoryPostProcessor(ppc);

			ctx.refresh();
			return ctx;
		} catch (Exception e) {
			throw new SlcException(
					"Cannot create SLC execution application context.", e);
		}
	}

	protected String findAntScript(SlcExecution slcExecution) {
		String scriptStr = slcExecution.getAttributes().get(
				AntConstants.EXECATTR_ANT_FILE);
		if (scriptStr == null)
			throw new SlcException("No Ant script provided");

		return scriptStr;
	}

	protected List<String> findAntTargets(SlcExecution slcExecution) {
		String targetList = slcExecution.getAttributes().get(
				AntConstants.EXECATTR_ANT_TARGETS);
		List<String> targets = new Vector<String>();
		if (targetList != null) {
			StringTokenizer stTargets = new StringTokenizer(targetList, ",");
			while (stTargets.hasMoreTokens()) {
				targets.add(stTargets.nextToken());
			}
		}
		return targets;
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

		ListableBeanFactory context = (ListableBeanFactory) project
				.getReference(AntConstants.REF_ROOT_CONTEXT);
		// Register build listeners
		Map<String, BuildListener> listeners = BeanFactoryUtils
				.beansOfTypeIncludingAncestors(context, BuildListener.class,
						false, false);
		for (BuildListener listener : listeners.values()) {
			project.addBuildListener(listener);
		}

		// Register log4j appenders from context
		MDC.put(AntConstants.MDC_ANT_PROJECT, project);
		Map<String, Appender> appenders = context.getBeansOfType(
				Appender.class, false, true);
		for (Appender appender : appenders.values()) {
			LogManager.getRootLogger().addAppender(appender);
		}

		project.init();
		addCustomTaskAndTypes(project);
	}

	/** Loads the SLC specific Ant tasks. */
	protected void addCustomTaskAndTypes(Project project) {
		Properties taskdefs = getDefs(project,
				AntConstants.SLC_TASKDEFS_RESOURCE_PATH);
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
				AntConstants.SLC_TYPEDEFS_RESOURCE_PATH);
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
			throw new SlcException("Cannot load task definitions", e);
		}
		return defs;
	}

	protected void initStructure(Project project, String scriptRelativePath) {
		// Init structure registry
		StructureRegistry<TreeSPath> registry = new TreeSRegistry();
		project.addReference(AntConstants.REF_STRUCTURE_REGISTRY, registry);

		// Lowest levels
		StringTokenizer st = new StringTokenizer(scriptRelativePath, "/");
		TreeSPath currPath = null;
		while (st.hasMoreTokens()) {
			String name = st.nextToken();
			if (currPath == null) {
				currPath = TreeSPath.createRootPath(name);
			} else {
				if (st.hasMoreTokens())// don't register project file
					currPath = currPath.createChild(name);
			}
			registry.register(currPath, new SimpleSElement(name));
		}

		// Project level
		String projectName = project.getName() != null
				&& !project.getName().equals("") ? project.getName()
				: "project";
		TreeSPath projectPath = currPath.createChild(projectName);

		String projectDesc = project.getDescription() != null
				&& !project.getDescription().equals("") ? project
				.getDescription() : projectPath.getName();

		registry.register(projectPath, new SimpleSElement(projectDesc));
		project.addReference(AntConstants.REF_PROJECT_PATH, projectPath);

		if (log.isDebugEnabled())
			log.debug("Project path: " + projectPath);
	}

	protected void parseProject(Project project, String scriptRelativePath) {
		try {
			Resource script = rootDir.createRelative(scriptRelativePath);
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
			throw new SlcException("Could not parse project for script "
					+ scriptRelativePath, e);
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
			throw new SlcException("SLC Ant execution failed", exception);
		} finally {
			p.fireBuildFinished(exception);
		}
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

	public void setWorkDir(File workDir) {
		this.workDir = workDir;
	}

	public void setParentContext(ConfigurableApplicationContext runtimeContext) {
		this.parentContext = runtimeContext;
	}

}
