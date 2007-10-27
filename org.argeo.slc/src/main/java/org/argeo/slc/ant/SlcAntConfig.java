package org.argeo.slc.ant;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.tools.ant.Project;

/** Load reference to directories from an slcRoot.properties file */
public class SlcAntConfig {
	// SLC ROOT PROPERTIES
	public final static String ROOT_DIR_PROPERTY = "org.argeo.slc.ant.rootDir";
	public final static String CONF_DIR_PROPERTY = "org.argeo.slc.ant.confDir";
	public final static String WORK_DIR_PROPERTY = "org.argeo.slc.ant.workDir";
	/**
	 * Comma-separated list of property file names to load from the conf dir and
	 * add to project user properties
	 */
	public final static String PROPERTY_FILE_NAMES_PROPERTY = "org.argeo.slc.ant.propertyFileNames";
	
	// SLC CONF PROPERTIES
	/** Path to the root Spring application context */
	public static String APPLICATION_CONTEXT_PROPERTY = "org.argeo.slc.ant.applicationContext";

	private final File confDir;
	private final File rootDir;
	private final File workDir;

	/** Retrieve all properties and set them as project user properties */
	public SlcAntConfig(Project project, File slcRootFile) {
		Properties p = loadFile(slcRootFile.getAbsolutePath());

		// Root dir
		rootDir = slcRootFile.getParentFile();
		project.setUserProperty(ROOT_DIR_PROPERTY, rootDir.getAbsolutePath());

		// Conf dir
		if (project.getUserProperty(CONF_DIR_PROPERTY) == null) {
			confDir = new File(p.getProperty(CONF_DIR_PROPERTY, rootDir
					.getAbsolutePath()
					+ "/../conf")).getAbsoluteFile();
			project.setUserProperty(CONF_DIR_PROPERTY, confDir
					.getAbsolutePath());
		} else {
			confDir = new File(project.getUserProperty(CONF_DIR_PROPERTY))
					.getAbsoluteFile();
		}

		// Work dir
		if (project.getUserProperty(WORK_DIR_PROPERTY) == null) {
			workDir = new File(p.getProperty(WORK_DIR_PROPERTY, rootDir
					.getAbsolutePath()
					+ "/../work")).getAbsoluteFile();
			project.setUserProperty(WORK_DIR_PROPERTY, workDir
					.getAbsolutePath());
		} else {
			workDir = new File(project.getUserProperty(WORK_DIR_PROPERTY))
					.getAbsoluteFile();
		}

		// Properties from the conf dir files
		Properties properties = new Properties();
		StringTokenizer st = new StringTokenizer(p.getProperty(
				PROPERTY_FILE_NAMES_PROPERTY, "slc.properties"), ",");
		while (st.hasMoreTokens()) {
			String fileName = st.nextToken();
			properties.putAll(loadFile(confDir.getAbsolutePath() + "/"
					+ fileName));
		}

		for (Object o : properties.keySet()) {
			String key = o.toString();
			if (project.getUserProperty(key) == null) {// not already set
				project.setUserProperty(key, properties.getProperty(key));
			}
		}

		// Default application context
		if (project.getUserProperty(APPLICATION_CONTEXT_PROPERTY) == null) {
			project.setUserProperty(APPLICATION_CONTEXT_PROPERTY, confDir
					.getAbsolutePath()
					+ "/applicationContext.xml");
		}
	}

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

	public File getConfDir() {
		return confDir;
	}

	public File getRootDir() {
		return rootDir;
	}

	public File getWorkDir() {
		return workDir;
	}

}
