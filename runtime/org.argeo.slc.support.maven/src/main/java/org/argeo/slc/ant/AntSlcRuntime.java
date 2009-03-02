package org.argeo.slc.ant;

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
import org.argeo.slc.SlcException;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.runtime.SlcExecutionOutput;
import org.argeo.slc.runtime.SlcRuntime;
import org.argeo.slc.spring.SpringUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public class AntSlcRuntime implements SlcRuntime<AntExecutionContext> {
	private final static Log log = LogFactory.getLog(AntSlcRuntime.class);

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
		if (slcRootFile == null)
			throw new SlcException(
					"Could not find any SLC root file, "
							+ "please configure one at the root of your scripts hierarchy.");

		// Create SlcExecution from arguments
		SlcExecution slcExecution = createSlcExecution(runtime, slcRootFile,
				scriptRes, targets);

		// Init application
		AntSlcApplication application = new AntSlcApplication();
		application.setSlcRootFile(slcRootFile);
		application.initFromSlcRootFile();

		// Execute test
		application.execute(slcExecution, properties, references,
				executionOutput);
	}

	protected Resource findScript(String scriptStr) {
		Resource scriptRes;
		if (new File(scriptStr).exists()) {
			scriptRes = new FileSystemResource(scriptStr);
		} else {
			scriptRes = new DefaultResourceLoader(Thread.currentThread()
					.getContextClassLoader()).getResource(scriptStr);
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

		slcExecution.setType(AntConstants.EXECTYPE_SLC_ANT);

		slcExecution.setUser(System.getProperty("user.name"));

		if (runtimeStr != null)
			slcExecution.getAttributes().put(AntConstants.EXECATTR_RUNTIME,
					runtimeStr);
		String scriptRelativePath = SpringUtils.extractRelativePath(SpringUtils
				.getParent(slcRootFile), script);

		slcExecution.getAttributes().put(AntConstants.EXECATTR_ANT_FILE,
				scriptRelativePath);
		if (targets != null)
			slcExecution.getAttributes().put(AntConstants.EXECATTR_ANT_TARGETS,
					targets);

		slcExecution.setStatus(SlcExecution.STATUS_SCHEDULED);
		return slcExecution;
	}

	/**
	 * Recursively scans directories downwards until it find a file name as
	 * defined by {@link #SLC_ROOT_FILE_NAME}.
	 */
	protected Resource findSlcRootFile(Resource currDir) {
		if (log.isTraceEnabled())
			log.trace("Look for SLC root file in " + currDir);

		try {
			Resource slcRootFile = currDir.createRelative(SLC_ROOT_FILE_NAME);
			if (slcRootFile.exists()) {
				if (log.isDebugEnabled())
					log.debug("Found SLC root file: " + slcRootFile);
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
}
