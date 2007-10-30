package org.argeo.slc.ant;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.StringTokenizer;

import org.springframework.util.Log4jConfigurer;

import org.apache.tools.ant.Project;

/** Load reference to directories from an slcRoot.properties file */
public class SlcAntConfig {
	// SLC ROOT PROPERTIES
	public final static String ROOT_FILE_PROPERTY = "slc.rootFile";
	public final static String ROOT_DIR_PROPERTY = "slc.rootDir";
	public final static String CONF_DIR_PROPERTY = "slc.confDir";
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
	public static String DIR_DESCRIPTION_PROPERTY = "slc.dirDescription";

	/**
	 * Retrieve all properties and set them as project user properties. Root
	 * properties (that is from slcRoot file) are added to System properties
	 * (e.g. in order to be used by Spring)
	 */
	public static void initProject(Project project, File slcRootFile) {
		System.getProperties().putAll(project.getUserProperties());
		System.setProperty(ROOT_FILE_PROPERTY, slcRootFile.getAbsolutePath());
		Properties all = prepareAllProperties();
		for (Object o : all.keySet()) {
			String key = o.toString();
			if (project.getUserProperty(key) == null) {// not already set
				project.setUserProperty(key, all.getProperty(key));
			}
		}
	}

	public static Properties prepareAllProperties() {
		try {
			Properties all = new Properties();
			all.putAll(System.getProperties());

			if (all.getProperty(ROOT_FILE_PROPERTY) == null) {
				throw new RuntimeException("System Property "
						+ ROOT_FILE_PROPERTY + " has to be set.");
			}

			File slcRootFile = new File(all.getProperty(ROOT_FILE_PROPERTY))
					.getAbsoluteFile();
			Properties rootProps = loadFile(slcRootFile.getAbsolutePath());

			final File confDir;
			final File workDir;
			// Root dir
			final File rootDir = slcRootFile.getParentFile();
			all.setProperty(ROOT_DIR_PROPERTY, rootDir.getCanonicalPath());

			// Conf dir
			if (all.getProperty(CONF_DIR_PROPERTY) == null) {
				confDir = new File(rootProps.getProperty(CONF_DIR_PROPERTY,
						rootDir.getAbsolutePath() + "/../conf"))
						.getCanonicalFile();
				all.setProperty(CONF_DIR_PROPERTY, confDir.getAbsolutePath());
			} else {
				confDir = new File(all.getProperty(CONF_DIR_PROPERTY))
						.getCanonicalFile();
			}

			// Work dir
			if (all.getProperty(WORK_DIR_PROPERTY) == null) {
				workDir = new File(rootProps.getProperty(WORK_DIR_PROPERTY,
						rootDir.getAbsolutePath() + "/../work"))
						.getCanonicalFile();
				all.setProperty(WORK_DIR_PROPERTY, workDir.getAbsolutePath());
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
				properties.putAll(loadFile(confDir.getAbsolutePath() + File.separator
						+ fileName));
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
				System.setProperty("log4j.configuration",confDir
						.getCanonicalPath()
						+ File.separator + "log4j.properties" );
				// TODO: fix dependency to log4j
				Log4jConfigurer.initLogging(confDir
						.getCanonicalPath()
						+ File.separator + "log4j.properties");
			}

			return all;
		} catch (Exception e) {
			throw new SlcAntException("Unexpected exception while configuring",
					e);
		}
	}

	public static Properties loadFile(String path) {
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

}
