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
