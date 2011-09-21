package org.argeo.slc.repo.maven.proxy;

/** Synchronizes JCR and Maven repositories */
public interface MavenProxyService {
	public String getNodePath(String path);

	public String proxyFile(String path);
}
