package org.argeo.slc.repo;

/** Coordinator of the various type of repository (Java, RPM, etc.) */
public interface SlcRepoManager {
	/** @return null if Java not supported. */
	public JavaRepoManager getJavaRepoManager();

	/** @return null if RPM not supported. */
	public RpmRepoManager getRpmRepoManager();
}
