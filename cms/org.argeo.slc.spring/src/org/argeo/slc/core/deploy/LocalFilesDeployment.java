package org.argeo.slc.core.deploy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.argeo.slc.SlcException;
import org.springframework.core.io.Resource;

public class LocalFilesDeployment implements Runnable {
	private String targetBase = "";
	private ResourceSet resourceSet;

	public LocalFilesDeployment() {
	}

	public LocalFilesDeployment(ResourceSet resourceSet) {
		this.resourceSet = resourceSet;
	}

	public void run() {
		Map<String, Resource> resources = resourceSet.listResources();
		for (String relPath : resources.keySet()) {
			File targetFile = new File(targetBase + File.separator + relPath);
			File parentDir = targetFile.getParentFile();
			if (!parentDir.exists())
				parentDir.mkdirs();

			Resource resource = resources.get(relPath);

			InputStream in = null;
			OutputStream out = null;
			try {
				in = resource.getInputStream();
				out = FileUtils.openOutputStream(targetFile);
				IOUtils.copy(in, out);
			} catch (IOException e) {
				throw new SlcException("Cannot extract " + resource + " to "
						+ targetFile, e);
			} finally {
				IOUtils.closeQuietly(in);
				IOUtils.closeQuietly(out);
			}
		}
	}

	public void setTargetBase(String targetBase) {
		this.targetBase = targetBase;
	}

	public void setResourceSet(ResourceSet resourceSet) {
		this.resourceSet = resourceSet;
	}

}
