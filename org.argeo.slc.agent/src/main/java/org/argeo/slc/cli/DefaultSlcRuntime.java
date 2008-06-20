package org.argeo.slc.cli;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.ant.AntSlcApplication;
import org.argeo.slc.ant.SlcAntConstants;
import org.argeo.slc.ant.SlcAntException;
import org.argeo.slc.core.SlcException;
import org.argeo.slc.core.process.SlcExecution;
import org.argeo.slc.runtime.SlcExecutionContext;
import org.argeo.slc.spring.SpringUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

public class DefaultSlcRuntime {
	private final static Log log = LogFactory.getLog(DefaultSlcRuntime.class);

	public final static String SLC_ROOT_FILE_NAME = "slcRoot.properties";

	public SlcExecutionContext executeScript(Resource script,
			Properties properties, Map<String, Object> references) {

		Resource slcRootFile = findSlcRootFile(script);
		String scriptRelativePath = SpringUtils.extractRelativePath(SpringUtils
				.getParent(slcRootFile), script);

		SlcExecution slcExecution = createSlcExecution();
		slcExecution.setStatus(SlcExecution.STATUS_RUNNING);
		slcExecution.getAttributes().put(SlcAntConstants.EXECATTR_ANT_FILE,
				scriptRelativePath);

		AntSlcApplication application = getApplication(slcRootFile);
		return application.execute(slcExecution, properties, references);
	}

	protected SlcExecution createSlcExecution() {
		SlcExecution slcExecution = new SlcExecution();
		slcExecution.setUuid(UUID.randomUUID().toString());
		try {
			slcExecution.setHost(InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			slcExecution.setHost(SlcExecution.UNKOWN_HOST);
		}

		slcExecution.setType(SlcAntConstants.EXECTYPE_SLC_ANT);

		slcExecution.setUser(System.getProperty("user.name"));
		return slcExecution;
	}

	protected AntSlcApplication getApplication(Resource slcRootFile) {
		AntSlcApplication application = new AntSlcApplication();
		InputStream inRootFile = null;
		try {
			// Remove basedir property in order to avoid conflict with Maven
			// if (all.containsKey("basedir"))
			// all.remove("basedir");

			inRootFile = slcRootFile.getInputStream();
			Properties rootProps = loadFile(inRootFile);

			Resource confDir = null;
			File workDir = null;
			// Root dir
			final Resource rootDir = SpringUtils.getParent(slcRootFile);

			// Conf dir
			String confDirStr = rootProps
					.getProperty(SlcAntConstants.CONF_DIR_PROPERTY);
			if (confDirStr != null)
				confDir = new DefaultResourceLoader(application.getClass()
						.getClassLoader()).getResource(confDirStr);

			if (confDir == null || !confDir.exists()) {
				// confDir = rootDir.createRelative("../conf");
				confDir = SpringUtils.getParent(rootDir)
						.createRelative("conf/");
			}

			// Work dir
			String workDirStr = rootProps
					.getProperty(SlcAntConstants.WORK_DIR_PROPERTY);
			if (workDirStr != null) {
				workDir = new File(workDirStr);
			}

			if (workDir == null || !workDir.exists()) {
				try {
					File rootDirAsFile = rootDir.getFile();
					workDir = new File(rootDirAsFile.getParent()
							+ File.separator + "work").getCanonicalFile();
				} catch (IOException e) {
					workDir = new File(System.getProperty("java.io.tmpdir")
							+ File.separator + "slcExecutions" + File.separator
							+ slcRootFile.getURL().getPath());
					log.debug("Root dir is not a file: " + e.getMessage()
							+ ", creating work dir in temp: " + workDir);
				}
				workDir.mkdirs();
			}

			application.setConfDir(confDir);
			application.setRootDir(rootDir);
			application.setWorkDir(workDir);

			return application;
		} catch (IOException e) {
			throw new SlcException(
					"Could not prepare SLC application for root file "
							+ slcRootFile, e);
		} finally {
			IOUtils.closeQuietly(inRootFile);
		}

		// Properties from the conf dir files
		// Properties properties = new Properties();
		// StringTokenizer st = new StringTokenizer(rootProps.getProperty(
		// PROPERTY_FILE_NAMES_PROPERTY, "slc.properties"), ",");
		// while (st.hasMoreTokens()) {
		// String fileName = st.nextToken();
		// properties.putAll(loadFile(confDir.getAbsolutePath()
		// + File.separator + fileName));
		// }
		//
		// for (Object o : properties.keySet()) {
		// String key = o.toString();
		// if (all.getProperty(key) == null) {// not already set
		// all.setProperty(key, properties.getProperty(key));
		// }
		// }
		//
	}

	/**
	 * Recursively scans directories downwards until it find a file name as
	 * defined by {@link #SLC_ROOT_FILE_NAME}.
	 */
	protected Resource findSlcRootFile(Resource currDir) {
		if (log.isDebugEnabled())
			log.debug("Look for SLC root file in " + currDir);

		try {
			Resource slcRootFile = currDir.createRelative(SLC_ROOT_FILE_NAME);
			if (slcRootFile.exists()) {
				return slcRootFile;
			} else {
				String currPath = currDir.getURL().getPath();
				if (currPath.equals("/") || currPath.equals("")) {
					return null;
				} else {
					return findSlcRootFile(SpringUtils.getParent(currDir));
				}
				// int indx = currPath.lastIndexOf('/',currPath.length()-1);

			}
		} catch (IOException e) {
			throw new SlcException("Problem when looking in SLC root file in "
					+ currDir, e);
		}

		// for (File file : dir.listFiles()) {
		// if (!file.isDirectory()
		// && file.getName().equals(SLC_ROOT_FILE_NAME)) {
		// return file;
		// }
		// }
		//
		// File parentDir = dir.getParentFile();
		// if (parentDir == null) {
		// return null;// stop condition: not found
		// } else {
		// return findSlcRootFile(parentDir);
		// }
	}

	/** Loads the content of a file as <code>Properties</code>. */
	private Properties loadFile(InputStream in) {
		Properties p = new Properties();
		try {
			p.load(in);
		} catch (IOException e) {
			throw new SlcAntException("Cannot read SLC root file", e);
		}
		return p;
	}

	// private Resource getParentOfFile(Resource file) {
	// try {
	// return file.createRelative(".");
	// } catch (IOException e) {
	// throw new SlcException("Cannot get parent for resource " + file, e);
	// }
	// }
}
