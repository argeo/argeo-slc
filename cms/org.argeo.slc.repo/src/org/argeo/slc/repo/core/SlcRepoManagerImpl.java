package org.argeo.slc.repo.core;

import org.argeo.slc.repo.JavaRepoManager;
import org.argeo.slc.repo.RpmRepoManager;
import org.argeo.slc.repo.SlcRepoManager;

/** Coordinator of the various repositories. */
public class SlcRepoManagerImpl implements SlcRepoManager {
	private JavaRepoManager javaRepoManager;
	private RpmRepoManager rpmRepoManager;

	public void init() {

	}

	public void destroy() {

	}

	@Override
	public JavaRepoManager getJavaRepoManager() {
		return javaRepoManager;
	}

	public void setJavaRepoManager(JavaRepoManager javaRepoManager) {
		this.javaRepoManager = javaRepoManager;
	}

	@Override
	public RpmRepoManager getRpmRepoManager() {
		return rpmRepoManager;
	}

	public void setRpmRepoManager(RpmRepoManager rpmRepoManager) {
		this.rpmRepoManager = rpmRepoManager;
	}

}
