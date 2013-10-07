package org.argeo.slc.repo;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/** OSGi Factory */
public interface OsgiFactory {
	public Session openJavaSession() throws RepositoryException;

	public Session openDistSession() throws RepositoryException;

	public void indexNode(Node node);

	/**
	 * Provide access to a third party archive in the 'dist' repository,
	 * downloading it if it is not available.
	 */
	public Node getDist(Session distSession, String uri)
			throws RepositoryException;

	/**
	 * Provide access to a cached maven ardifact identified by its coordinates
	 * the 'dist' repository, downloading it if it is not available.
	 */
	public Node getMaven(Session distSession, String coords)
			throws RepositoryException;
}
