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
import org.argeo.slc.ant.AntExecutionContext;
import org.argeo.slc.ant.AntSlcApplication;
import org.argeo.slc.ant.SlcAntConstants;
import org.argeo.slc.core.SlcException;
import org.argeo.slc.core.process.SlcExecution;
import org.argeo.slc.runtime.SlcExecutionOutput;
import org.argeo.slc.spring.SpringUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public class DefaultSlcRuntime {
	private final static Log log = LogFactory.getLog(DefaultSlcRuntime.class);

	public final static String SLC_ROOT_FILE_NAME = "slcRoot.properties";

	/**
	 * Simplified execution with default runtime, default target, and no
	 * properties/reference arguments.
	 * 
	 * @param script
	 *            path to the script
	 * @param executionOutput
	 *            output
	 * 
	 * @see #executeScript(String, String, String, Properties, Map,
	 *      SlcExecutionOutput)
	 */
	public void executeScript(String script,
			SlcExecutionOutput<AntExecutionContext> executionOutput) {
		executeScript(null, script, null, null, null, executionOutput);
	}

	/**
	 * Simplified execution with default runtime, and no properties/reference
	 * arguments.
	 * 
	 * @param script
	 *            path to the script
	 * @param targets
	 *            comma separated list of targets
	 * @param executionOutput
	 *            output
	 * @see #executeScript(String, String, String, Properties, Map,
	 *      SlcExecutionOutput)
	 */
	public void executeScript(String script, String targets,
			SlcExecutionOutput<AntExecutionContext> executionOutput) {
		executeScript(null, script, targets, null, null, executionOutput);
	}

	public void executeScript(String runtime, String script, String targets,
			Properties properties, Map<String, Object> references,
			SlcExecutionOutput<AntExecutionContext> executionOutput) {

		Resource scriptRes = findScript(script);
		Resource slcRootFile = findSlcRootFile(scriptRes);

		SlcExecution slcExecution = createSlcExecution(runtime, slcRootFile,
				scriptRes, targets);

		AntSlcApplication application = getApplication(slcRootFile);
		application.execute(slcExecution, properties, references,
				executionOutput);
	}

	protected Resource findScript(String scriptStr) {
		Resource scriptRes;
		if (new File(scriptStr).exists()) {
			scriptRes = new FileSystemResource(scriptStr);
		} else {
			scriptRes = new DefaultResourceLoader(SlcMain.class
					.getClassLoader()).getResource(scriptStr);
		}
		return scriptRes;
	}

	protected SlcExecution createSlcExecution(String runtimeStr,
			Resource slcRootFile, Resource script, String targets) {
		SlcExecution slcExecution = new SlcExecution();
		slcExecution.setUuid(UUID.randomUUID().toString());
		try {
			slcExecution.setHost(InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			slcExecution.setHost(SlcExecution.UNKOWN_HOST);
		}

		slcExecution.setType(SlcAntConstants.EXECTYPE_SLC_ANT);

		slcExecution.setUser(System.getProperty("user.name"));

		if (runtimeStr != null)
			slcExecution.getAttributes().put(SlcAntConstants.EXECATTR_RUNTIME,
					runtimeStr);
		String scriptRelativePath = SpringUtils.extractRelativePath(SpringUtils
				.getParent(slcRootFile), script);

		slcExecution.getAttributes().put(SlcAntConstants.EXECATTR_ANT_FILE,
				scriptRelativePath);
		if (targets != null)
			slcExecution.getAttributes().put(
					SlcAntConstants.EXECATTR_ANT_TARGETS, targets);

		slcExecution.setStatus(SlcExecution.STATUS_SCHEDULED);
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
			}
		} catch (IOException e) {
			throw new SlcException("Problem when looking in SLC root file in "
					+ currDir, e);
		}
	}

	/** Loads the content of a file as <code>Properties</code>. */
	private Properties loadFile(InputStream in) {
		Properties p = new Properties();
		try {
			p.load(in);
		} catch (IOException e) {
			throw new SlcException("Cannot read SLC root file", e);
		}
		return p;
	}
}
