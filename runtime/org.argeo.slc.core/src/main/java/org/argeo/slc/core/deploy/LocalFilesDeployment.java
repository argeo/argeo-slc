/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
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
