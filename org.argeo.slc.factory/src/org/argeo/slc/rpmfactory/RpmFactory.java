package org.argeo.slc.rpmfactory;

import java.io.File;
import java.util.List;

import javax.jcr.Node;

/**
 * Defines a build environment. This information is typically used by other
 * components performing the various actions related to RPM build.
 */
public interface RpmFactory {
	//
	// DIRECT ACTIONS ON JCR REPOSITORY
	//
	public void indexWorkspace(String workspace);

	public Node newDistribution(String distributionId);

	//
	// CONFIG FILES GENERATION
	//
	/** Creates a mock config file. */
	public File getMockConfigFile(String arch, String branch);

	/** Creates a yum config file. */
	public File getYumRepoFile(String arch);

	//
	// WORKSPACES
	//
	public String getStagingWorkspace();

	/**
	 * @return the name of the testing workspace, or null if and only if the
	 *         testing workspace was not enabled.
	 */
	public String getTestingWorkspace();

	public String getStableWorkspace();

	public File getWorkspaceDir(String workspace);

	//
	// ARCH DEPENDENT INFOS
	//
	public List<String> getArchs();

	public String getMockConfig(String arch);

	public String getIdWithArch(String arch);

	public File getResultDir(String arch);

	//
	// DEPLOYMENT
	//
	public String getGitBaseUrl();

	public Boolean isDeveloperInstance();

}
