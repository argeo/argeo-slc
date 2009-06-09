package org.argeo.slc.core.execution;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.execution.ExecutionContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

public class FileExecutionResources implements ExecutionResources {
	private final static Log log = LogFactory
			.getLog(FileExecutionResources.class);
	protected final static String DEFAULT_EXECUTION_RESOURCES_DIRNAME = "executionResources";
	public final static String DEFAULT_EXECUTION_RESOURCES_TMP_PATH = System
			.getProperty("java.io.tmpdir")
			+ File.separator
			+ "slc"
			+ File.separator
			+ DEFAULT_EXECUTION_RESOURCES_DIRNAME;

	private File baseDir;
	private ExecutionContext executionContext;
	private String prefixDatePattern = "yyyyMMdd_HHmmss_";
	private SimpleDateFormat sdf = null;

	private Boolean withExecutionSubdirectory = true;

	public FileExecutionResources() {
		// Default base directory
		String osgiInstanceArea = System.getProperty("osgi.instance.area");
		String osgiInstanceAreaDefault = System
				.getProperty("osgi.instance.area.default");
		String tempDir = System.getProperty("java.io.tmpdir");

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

	public String getAsOsPath(Resource resource, Boolean overwrite) {
		File file = null;
		try {
			file = resource.getFile();
			return file.getCanonicalPath();
		} catch (IOException e) {
			if (log.isTraceEnabled())
				log
						.trace("Resource "
								+ resource
								+ " is not available on the file system. Retrieving it...");
		}

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

	public File getFile(String relativePath) {
		Assert.notNull(executionContext, "execution context is null");

		if (withExecutionSubdirectory) {
			String path = baseDir.getPath() + File.separator
					+ sdf().format(executionContext.getCreationDate())
					+ executionContext.getUuid();
			File executionDir = new File(path);

			return new File(executionDir.getPath() + File.separator
					+ relativePath.replace('/', File.separatorChar));
		} else {
			return new File(baseDir.getPath() + File.separator
					+ relativePath.replace('/', File.separatorChar));
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

	public void setWithExecutionSubdirectory(Boolean withExecutionSubdirectory) {
		this.withExecutionSubdirectory = withExecutionSubdirectory;
	}

}
