package org.argeo.slc.core.execution;

import java.io.File;
import java.text.SimpleDateFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.execution.ExecutionContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

public class FileExecutionResources implements ExecutionResources {
	private final static Log log = LogFactory
			.getLog(FileExecutionResources.class);
	protected final static String DEFAULT_EXECUTION_RESOURCES_DIRNAME = "executionResources";

	private File baseDir;
	private ExecutionContext executionContext;
	private String prefixDatePattern = "yyyyMMdd_HHmmss_";
	private SimpleDateFormat sdf = null;

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
			baseDir = new File(tempDir + File.separator + "slc"
					+ File.separator + DEFAULT_EXECUTION_RESOURCES_DIRNAME);
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
			if (log.isTraceEnabled())
				log.trace("Creating parent directory " + parentDir);
			parentDir.mkdirs();
		}
		Resource resource = new FileSystemResource(file);
		if (log.isTraceEnabled())
			log.trace("Returns writable resource " + resource);
		return resource;
	}

	public File getFile(String relativePath) {
		Assert.notNull(executionContext, "execution context is null");

		String path = baseDir.getPath() + File.separator
				+ sdf().format(executionContext.getCreationDate())
				+ executionContext.getUuid();
		File executionDir = new File(path);

		// Creates if necessary
		if (!executionDir.exists()) {
			if (log.isDebugEnabled())
				log.debug("Creating execution directory " + executionDir);
			executionDir.mkdirs();
		}

		return new File(executionDir.getPath() + File.separator
				+ relativePath.replace('/', File.separatorChar));
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
}
