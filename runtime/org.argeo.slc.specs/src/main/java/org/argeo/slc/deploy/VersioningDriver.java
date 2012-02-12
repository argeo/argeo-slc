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

package org.argeo.slc.deploy;

import java.io.File;
import java.io.OutputStream;
import java.util.List;

/** Abstracts common versioning operations */
public interface VersioningDriver {
	public void getFileFromRepository(String repositoryBaseUrl,
			String location, OutputStream out);

	public List<String> getChangedPaths(File repositoryRoot, Long revision);

	public String getRepositoryRoot(String repositoryUrl);

	public String getRelativePath(String repositoryUrl);

	public void updateToHead(File fileOrDir);

	public void importFileOrDir(String repositoryUrl, File fileOrDir);

	/**
	 * Checks out or update this versioned directory
	 * 
	 * @return true if the content has changed, false otherwise
	 */
	public Boolean checkout(String repositoryUrl, File destDir,
			Boolean recursive);

	public void createRepository(String filePath);

	public void commit(File fileOrDir, String commitMessage);
}
