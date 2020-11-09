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
