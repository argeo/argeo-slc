package org.argeo.slc.ant;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.springframework.util.Log4jConfigurer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.Project;

/**
 * <p>
 * Manager and initializer of the properties required by SLC Ant.
 * </p>
 * 
 * <p>
 * All properties described here will get a value one way or another (see below
 * for details)/ Each property will be accessible via Ant or Spring properties.
 * </p>
 * 
 * <p>
 * The property <i>slc.rootFile</i> is set based on the location of the SLC
 * root property file found in the directory structure of a called Ant file. The
 * default name of this file is <b>slcRoot.properties</b> (can be set by
 * {@link #setSlcRootFileName(String)}). <br>
 * This property provides the absolute path to the unique SLC root property file
 * which marks the root of an Ant SLC tree structure.
 * </p>
 * 
 * <p>
 * The property <i>slc.rootDir</i> is inferred from <i>slc.rootFile</i> and
 * provides a convenient shortcut to the root directory of the Ant files
 * directory structure.
 * </p>
 * 
 * <p>
 * A few directory and file related properties can be set in the SLC root
 * property file (if they are not explicitly set their default values will be
 * used):
 * 
 * <table border="1" cellspacing="0">
 * <tr>
 * <th>Property</th>
 * <th>Description</th>
 * <th>Default</th>
 * </tr>
 * <tr>
 * <td><i>slc.confDir</i></td>
 * <td>Directory where to find the various configuration files of a given SLC
 * Ant deployment</td>
 * <td>${slc.rootDir}/../conf</td>
 * </tr>
 * <tr>
 * <td><i>slc.workDir</i></td>
 * <td>Directory where data can be retrieved or generated: build outputs, test
 * inputs/outputs, test results, etc. The underlying directory structure is
 * specified by the specific SLC application.</td>
 * <td>${slc.rootDir}/../work</td>
 * </tr>
 * <tr>
 * <td><i>slc.propertyFileNames</i></td>
 * <td>Comma-separated list of the files names of the property files to load
 * from the conf directory. Having various files allows to separate between SLC
 * framework properties and properties specific to a given application built on
 * top of SLC. All will be available across Ant and Spring.</td>
 * <td>slc.properties</td>
 * </tr>
 * </table> <b>Note:</b> Only the properties above can be set in the SLC root
 * properties file. All other properties should be defined in the registered
 * conf files.
 * </p>
 * 
 * <p>
 * Any property can be defined in the conf files defined in the SLC root
 * properties file (see above). SLC expects some which will have defaults but
 * can be overriden there. By convention they should be defined in the
 * <b>slc.properties</b> file, while application specific properties should be
 * defined in other conf files. This allows for a clean spearation between SLC
 * and the applications built on top of it:
 * 
 * <table border="1" cellspacing="0">
 * <tr>
 * <th>Property</th>
 * <th>Description</th>
 * <th>Default</th>
 * </tr>
 * <tr>
 * <td><i>slc.applicationContext</i></td>
 * <td>Path to the root Spring application context file used by SLC Ant.</td>
 * <td>${slc.confDir}/applicationContext.xml</td>
 * </tr>
 * <tr>
 * <td><i>slc.defaultTestRun</i></td>
 * <td>Name of the {@link WritableTestRun} Spring bean that the
 * <code>slc.test</code> task will use by default. This can be overridden when
 * calling the task from Ant.</td>
 * <td>defaultTestRun</td>
 * </tr>
 * </table>
 * </p>
 */
public class SlcAntConfig {
	// SLC ROOT PROPERTIES
	/** Property for the root file (SLC root property file). */
	public final static String ROOT_FILE_PROPERTY = "slc.rootFile";
	/** Property for the root dir (SLC root property file). */
	public final static String ROOT_DIR_PROPERTY = "slc.rootDir";
	/** Property for the conf dir (SLC root property file). */
	public final static String CONF_DIR_PROPERTY = "slc.confDir";
	/** Property for the work dir (SLC root property file). */
	public final static String WORK_DIR_PROPERTY = "slc.workDir";
	/**
	 * Comma-separated list of property file names to load from the conf dir and
	 * add to project user properties
	 */
	public final static String PROPERTY_FILE_NAMES_PROPERTY = "slc.propertyFileNames";

	// SLC CONF PROPERTIES
	/** Path to the root Spring application context */
	public static String APPLICATION_CONTEXT_PROPERTY = "slc.applicationContext";
	/** Name of the Spring bean used by default */
	public static String DEFAULT_TEST_RUN_PROPERTY = "slc.defaultTestRun";

	// SLC LOCAL PROPERTIES
	/** Property for the dir label (SLC local property file). */
	public static String DIR_LABEL_PROPERTY = "slc.dirLabel";

	private String slcRootFileName = "slcRoot.properties";
	private String slcLocalFileName = "slcLocal.properties";

	/**
	 * Retrieves or infers all properties and set them as project user
	 * properties. All these properties will be set as project properties <b>if
	 * they had not been set as project properties before</b> (like by
	 * overriding through the standard Ant mechanisms).
	 * 
	 * @param project
	 *            the Ant <code>Project</code> being run.
	 * @return whether the project could be initialized for SLC usage (e.g.
	 *         presence of an SLC root file)
	 */
	public boolean initProject(Project project) {
		File projectBaseDir = project.getBaseDir();
		File slcRootFile = findSlcRootFile(projectBaseDir);
		if (slcRootFile == null) {
			return false;
		}

		// pass the project properties through the System properties
		System.getProperties().putAll((Map<?, ?>) project.getUserProperties());
		Properties all = new Properties();
		all.putAll(System.getProperties());
		prepareAllProperties(slcRootFile,all);

		Log log = LogFactory.getLog(this.getClass());
		for (Object o : all.keySet()) {
			String key = o.toString();
			// System.out.println(key+"="+all.getProperty(key));
			if (project.getUserProperty(key) == null) {// not already set
//				if (log.isDebugEnabled())
//					log.debug(key + "=" + all.getProperty(key));
				project.setUserProperty(key, all.getProperty(key));
			}
		}
		return true;
	}

	/**
	 * Retrieves or infers all required properties.
	 * 
	 * @param slcRootFile
	 *            the location of the SLC root file
	 * 
	 * @return the prepared properties. Note that it also contains the System
	 *         and Ant properties which had previously been set.
	 */
	public void prepareAllProperties(File slcRootFile, Properties all) {
		try {
			final String fileUrlPrefix = "";

			all.put(ROOT_FILE_PROPERTY, slcRootFile.getCanonicalPath());
			// Remove basedir property in order to avoid conflict with Maven
			if (all.containsKey("basedir"))
				all.remove("basedir");

			Properties rootProps = loadFile(slcRootFile.getCanonicalPath());

			final File confDir;
			final File workDir;
			// Root dir
			final File rootDir = slcRootFile.getParentFile();
			all.setProperty(ROOT_DIR_PROPERTY, fileUrlPrefix
					+ rootDir.getCanonicalPath());

			// Conf dir
			if (all.getProperty(CONF_DIR_PROPERTY) == null) {
				confDir = new File(rootProps.getProperty(CONF_DIR_PROPERTY,
						rootDir.getAbsolutePath() + "/../conf"))
						.getCanonicalFile();
				all.setProperty(CONF_DIR_PROPERTY, fileUrlPrefix
						+ confDir.getAbsolutePath());
			} else {
				confDir = new File(all.getProperty(CONF_DIR_PROPERTY))
						.getCanonicalFile();
			}

			// Work dir
			if (all.getProperty(WORK_DIR_PROPERTY) == null) {
				workDir = new File(rootProps.getProperty(WORK_DIR_PROPERTY,
						rootDir.getAbsolutePath() + "/../work"))
						.getCanonicalFile();
				all.setProperty(WORK_DIR_PROPERTY, fileUrlPrefix
						+ workDir.getAbsolutePath());
			} else {
				workDir = new File(all.getProperty(WORK_DIR_PROPERTY))
						.getCanonicalFile();
			}

			// Properties from the conf dir files
			Properties properties = new Properties();
			StringTokenizer st = new StringTokenizer(rootProps.getProperty(
					PROPERTY_FILE_NAMES_PROPERTY, "slc.properties"), ",");
			while (st.hasMoreTokens()) {
				String fileName = st.nextToken();
				properties.putAll(loadFile(confDir.getAbsolutePath()
						+ File.separator + fileName));
			}

			for (Object o : properties.keySet()) {
				String key = o.toString();
				if (all.getProperty(key) == null) {// not already set
					all.setProperty(key, properties.getProperty(key));
				}
			}

			// Default application context
			if (all.getProperty(APPLICATION_CONTEXT_PROPERTY) == null) {
				all.setProperty(APPLICATION_CONTEXT_PROPERTY, confDir
						.getAbsolutePath()
						+ "/applicationContext.xml");
			}
			// Default test run
			if (all.getProperty(DEFAULT_TEST_RUN_PROPERTY) == null) {
				all.setProperty(DEFAULT_TEST_RUN_PROPERTY, "defaultTestRun");
			}

			// Default log4j
			if (all.getProperty("log4j.configuration") == null) {
				System.setProperty("log4j.configuration", confDir
						.getCanonicalPath()
						+ File.separator + "log4j.properties");
				// TODO: fix dependency to log4j
				Log4jConfigurer.initLogging(confDir.getCanonicalPath()
						+ File.separator + "log4j.properties");
			}
		} catch (Exception e) {
			throw new SlcAntException("Unexpected exception while configuring",
					e);
		}
	}

	/** Loads the content of a file as <code>Properties</code>. */
	private Properties loadFile(String path) {
		Properties p = new Properties();
		try {
			FileInputStream in = new FileInputStream(path);
			p.load(in);
			in.close();
		} catch (IOException e) {
			throw new SlcAntException("Cannot read SLC root file", e);
		}
		return p;
	}

	/**
	 * Looks for a file named {@link #getSlcLocalFileName()} in the directory,
	 * loads it as properties file and return the value of the property
	 * {@link #DIR_LABEL_PROPERTY}.
	 */
	public String getDescriptionForDir(File dir) {
		String description = dir.getName();
		File slcLocal = new File(dir.getPath() + File.separator
				+ getSlcLocalFileName());
		if (slcLocal.exists()) {
			Properties properties = loadFile(slcLocal.getAbsolutePath());
			description = properties.getProperty(
					SlcAntConfig.DIR_LABEL_PROPERTY, description);
		}
		return description;
	}

	/**
	 * Recursively scans directories downwards until it find a file names as
	 * defined by {@link #getSlcRootFileName()}.
	 */
	public File findSlcRootFile(File dir) {
		for (File file : dir.listFiles()) {
			if (!file.isDirectory()
					&& file.getName().equals(getSlcRootFileName())) {
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

	/**
	 * Gets the file name of the file marking the root directory, default being
	 * <i>slcRoot.properties</i>.
	 */
	public String getSlcRootFileName() {
		return slcRootFileName;
	}

	/** Sets the file name of the file marking the root directory. */
	public void setSlcRootFileName(String slcRootFileName) {
		this.slcRootFileName = slcRootFileName;
	}

	/**
	 * Gets the file name of the file containing directory specific properties,
	 * default being <i>slcLocal.properties</i>.
	 */
	public String getSlcLocalFileName() {
		return slcLocalFileName;
	}

	/** Sets the file name of the file containing directory specific properties. */
	public void setSlcLocalFileName(String slcLocalFileName) {
		this.slcLocalFileName = slcLocalFileName;
	}

}
