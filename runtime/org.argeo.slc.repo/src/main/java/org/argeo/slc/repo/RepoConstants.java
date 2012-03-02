package org.argeo.slc.repo;

/** SLC repository constants */
public interface RepoConstants {
	public final static String ARTIFACTS_BASE_PATH = "/";
	public final static String REPO_BASEPATH = "/slc:repo";
	public final static String PROXIED_REPOSITORIES = REPO_BASEPATH
			+ "/slc:sources";
	public final static String DISTRIBUTIONS_BASE_PATH = REPO_BASEPATH
			+ "/slc:distributions";
}
