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
package org.argeo.slc.repo;

/** SLC repository constants */
public interface RepoConstants {
	String DEFAULT_JAVA_REPOSITORY_ALIAS = "java";
	String DEFAULT_JAVA_REPOSITORY_LABEL = "Internal Java Repository";


	String DEFAULT_ARTIFACTS_BASE_PATH = "/";
	String REPO_BASEPATH = "/slc:repo";
	String PROXIED_REPOSITORIES = REPO_BASEPATH + "/slc:sources";
	String DISTRIBUTIONS_BASE_PATH = REPO_BASEPATH + "/slc:distributions";
	String REPOSITORIES_BASE_PATH = REPO_BASEPATH + "/slc:repositories";
	String DIST_DOWNLOAD_BASEPATH = "/download";

	String BINARIES_ARTIFACT_ID = "binaries";
	String SOURCES_ARTIFACT_ID = "sources";
	String SDK_ARTIFACT_ID = "sdk";

	// TODO might exists somewhere else
	String SLC_CATEGORY_ID = "SLC-Category";

	// TODO find a more generic way
	String DEFAULT_DEFAULT_WORKSPACE = "main";
}
