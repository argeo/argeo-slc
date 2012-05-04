package org.argeo.slc.repo.maven;

import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.qom.QueryObjectModelFactory;

import org.argeo.jcr.JcrUtils;

/**
 * Migrate the distribution from 1.2 to 1.4 by cleaning naming and dependencies.
 * The dependency to the SpringSource Enterprise Bundle repository is removed as
 * well as theire naming convention. All third party are move to org.argeo.tp
 * group IDs. Maven dependency for Eclipse artifacts don't use version ranges
 * anymore. Verison constraints on javax.* packages are removed (since they lead
 * to "use package conflicts" when Eclipse and Spring Security are used
 * together).
 */
public class Migration_01_03 implements Runnable {

	private Repository repository;
	private String sourceWorkspace;
	private String targetWorkspace;

	private Session sourceSession;
	private Session targetSession;

	public void init() throws RepositoryException {
		sourceSession = JcrUtils.loginOrCreateWorkspace(repository,
				sourceWorkspace);
		targetSession = JcrUtils.loginOrCreateWorkspace(repository,
				targetWorkspace);
	}

	public void destroy() {
		JcrUtils.logoutQuietly(sourceSession);
		JcrUtils.logoutQuietly(targetSession);
	}

	public void run() {

	}

	static NodeIterator listArtifactVersions(Session session)
			throws RepositoryException {
		QueryObjectModelFactory qomf = session.getWorkspace().getQueryManager()
				.getQOMFactory();
		return null;
	}
}
