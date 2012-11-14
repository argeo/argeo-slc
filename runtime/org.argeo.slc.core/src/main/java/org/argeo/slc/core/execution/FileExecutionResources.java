/*
 * Copyright (C) 2007-2012 Argeo GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.argeo.slc.core.execution;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.execution.ExecutionContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/** Implements write access to resources based on standard Java {@link File} */
public class FileExecutionResources implements ExecutionResources {
	private final static Log log = LogFactory
			.getLog(FileExecutionResources.class);
	protected final static String DEFAULT_EXECUTION_RESOURCES_DIRNAME = "executionResources";
	public final static String DEFAULT_EXECUTION_RESOURCES_TMP_PATH = System
			.getProperty("java.io.tmpdir")
			+ File.separator
			+ System.getProperty("user.name")
			+ File.separator
			+ "slc"
			+ File.separator + DEFAULT_EXECUTION_RESOURCES_DIRNAME;

	private File baseDir;
	private ExecutionContext executionContext;
	private String prefixDatePattern = "yyMMdd_HHmmss_SSS";
	private SimpleDateFormat sdf = null;

	private Boolean withExecutionSubdirectory = true;

	public FileExecutionResources() {
		// Default base directory
		String osgiInstanceArea = System.getProperty("osgi.instance.area");
		String osgiInstanceAreaDefault = System
				.getProperty("osgi.instance.area.default");

		if (osgiInstanceArea != null) {
			// within OSGi with -data specified
			osgiInstanceArea = removeFilePrefix(osgiInstanceArea);
			baseDir = new File(osgiInstanceArea + File.separator
					+ DEFAULT_EXECUTION_RESOURCES_DIRNAME);
		} else if (osgiInstanceAreaDefault != null) {
			// within OSGi without -data specified
			osgiInstanceAreaDefault = removeFilePrefix(osgiInstanceAreaDefault);
			baseDir = new File(osgiInstanceAreaDefault + File.separator
					+ DEFAULT_EXECUTION_RESOURCES_DIRNAME);
		} else {// outside OSGi
			baseDir = new File(DEFAULT_EXECUTION_RESOURCES_TMP_PATH);
		}
	}

	protected SimpleDateFormat sdf() {
		// Lazy init in case prefix has been externally set
		if (sdf == null)
			sdf = new SimpleDateFormat(prefixDatePattern);
		return sdf;
	}

	public Resource getWritableResource(String relativePath) {
		File file = getFile(relativePath);
		File parentDir = file.getParentFile();

		if (!parentDir.exists()) {
			// Creates if necessary
			if (log.isTraceEnabled())
				log.trace("Creating parent directory " + parentDir);
			parentDir.mkdirs();
		}
		Resource resource = new FileSystemResource(file);

		if (log.isTraceEnabled())
			log.trace("Returns writable resource " + resource);
		return resource;
	}

	public String getWritableOsPath(String relativePath) {
		try {
			return getFile(relativePath).getCanonicalPath();
		} catch (IOException e) {
			throw new SlcException("Cannot find canonical path", e);
		}
	}

	public File getWritableOsFile(String relativePath) {
		return getFile(relativePath);
	}

	public String getAsOsPath(Resource resource, Boolean overwrite) {
		File file = fileFromResource(resource);
		if (file != null)
			try {
				if (log.isTraceEnabled())
					log.debug("Directly interpret " + resource + " as OS file "
							+ file);
				return file.getCanonicalPath();
			} catch (IOException e1) {
				// silent
			}

		if (log.isTraceEnabled())
			log.trace("Resource " + resource
					+ " is not available on the file system. Retrieving it...");

		InputStream in = null;
		OutputStream out = null;
		try {
			String path = resource.getURL().getPath();
			file = getFile(path);
			if (file.exists() && !overwrite)
				return file.getCanonicalPath();

			file.getParentFile().mkdirs();
			in = resource.getInputStream();
			out = new FileOutputStream(file);
			IOUtils.copy(in, out);
			if (log.isDebugEnabled())
				log.debug("Retrieved " + resource + " to OS file " + file);
			return file.getCanonicalPath();
		} catch (IOException e) {
			throw new SlcException("Could not make resource " + resource
					+ " an OS file.", e);
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
	}

	/**
	 * Extract the underlying file from the resource.
	 * 
	 * @return the file or null if no files support this resource.
	 */
	protected File fileFromResource(Resource resource) {
		try {
			return resource.getFile();
		} catch (IOException e) {
			return null;
		}

	}

	protected File getFile(String relativePath) {
		File writableBaseDir = getWritableBaseDir();
		return new File(writableBaseDir.getPath() + File.separator
				+ relativePath.replace('/', File.separatorChar));
	}

	public File getWritableBaseDir() {
		if (withExecutionSubdirectory) {
			Date executionContextCreationDate = (Date) executionContext
					.getVariable(ExecutionContext.VAR_EXECUTION_CONTEXT_CREATION_DATE);
			Assert.notNull(executionContext, "execution context is null");
			String path = baseDir.getPath() + File.separator
					+ sdf().format(executionContextCreationDate);
			// TODO write execution id somewhere? like in a txt file
			return new File(path);
		} else {
			return baseDir;
		}
	}

	protected String removeFilePrefix(String url) {
		if (url.startsWith("file:"))
			return url.substring("file:".length());
		else if (url.startsWith("reference:file:"))
			return url.substring("reference:file:".length());
		else
			return url;
	}

	public void setBaseDir(File baseDir) {
		this.baseDir = baseDir;
	}

	public void setExecutionContext(ExecutionContext executionContext) {
		this.executionContext = executionContext;
	}

	public void setPrefixDatePattern(String prefixDatePattern) {
		this.prefixDatePattern = prefixDatePattern;
	}

	public File getBaseDir() {
		return baseDir;
	}

	public ExecutionContext getExecutionContext() {
		return executionContext;
	}

	public String getPrefixDatePattern() {
		return prefixDatePattern;
	}

	/** Default is true. */
	public void setWithExecutionSubdirectory(Boolean withExecutionSubdirectory) {
		this.withExecutionSubdirectory = withExecutionSubdirectory;
	}

}
