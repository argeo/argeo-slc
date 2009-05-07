package org.argeo.slc.core.execution;

import java.io.File;
import java.text.SimpleDateFormat;

import org.argeo.slc.execution.ExecutionContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public class FileExecutionResources implements ExecutionResources,
		InitializingBean {
	private File baseDir;
	private ExecutionContext executionContext;
	private String prefixDatePattern = "yyyyMMdd_HHmmss_";
	private SimpleDateFormat sdf = null;

	public void afterPropertiesSet() throws Exception {
		if (sdf == null)
			sdf = new SimpleDateFormat(prefixDatePattern);

		if (baseDir == null) {
			String osgiInstanceArea = System.getProperty("osgi.instance.area");
			if (osgiInstanceArea != null) {
				if (osgiInstanceArea.startsWith("file:"))
					osgiInstanceArea = osgiInstanceArea.substring("file:"
							.length());
				baseDir = new File(osgiInstanceArea + File.separator
						+ "executionResources");
			}

			if (baseDir == null) {
				String tempDir = System.getProperty("java.io.tmpdir");
				baseDir = new File(tempDir + File.separator
						+ "slcExecutionResources");
			}
		}
	}

	public Resource getWritableResource(String relativePath) {
		File file = getFile(relativePath);
		file.getParentFile().mkdirs();
		return new FileSystemResource(file);
	}

	public File getFile(String relativePath) {
		File executionDir = new File(baseDir.getPath() + File.separator
				+ sdf.format(executionContext.getCreationDate())
				+ executionContext.getUuid());
		if (!executionDir.exists())
			executionDir.mkdirs();
		return new File(executionDir.getPath() + File.separator + relativePath);
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

	public void setSdf(SimpleDateFormat sdf) {
		this.sdf = sdf;
	}

}
