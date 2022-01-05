package org.argeo.slc.repo;

import org.argeo.api.cms.CmsConstants;

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
	String DEFAULT_DEFAULT_WORKSPACE = CmsConstants.SYS_WORKSPACE;
}
