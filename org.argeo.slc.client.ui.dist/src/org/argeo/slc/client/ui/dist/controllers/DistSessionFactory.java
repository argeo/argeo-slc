package org.argeo.slc.client.ui.dist.controllers;

import javax.jcr.Credentials;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.RepositoryFactory;
import javax.jcr.Session;

import org.argeo.api.security.Keyring;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.repo.RepoUtils;

/**
 * Provide shortcuts to retrieve sessions, repositories and workspaces that are
 * persisted in the current user node using path only.
 */
public class DistSessionFactory {

	/* DEPENDENCY INJECTION */
	private RepositoryFactory repositoryFactory;
	private Keyring keyring;
	private Repository nodeRepository;

	/**
	 * Returns a new session on the given workspace. This session *must* be
	 * disposed by the caller. If the workspace does not exist and
	 * createIfNeeded==true, tries to create it
	 * 
	 * */
	public Session getSessionFromWorkspacePath(String path,
			boolean createIfNeeded) {
		Session nodeSession = null;
		try {
			nodeSession = nodeRepository.login();
			Node localWksp = nodeSession.getNode(path);
			Repository repository = RepoUtils.getRepository(repositoryFactory,
					keyring, localWksp.getParent());
			Credentials credentials = RepoUtils.getRepositoryCredentials(
					keyring, localWksp.getParent());

			String wkspName = JcrUtils.lastPathElement(path);
			Session session = null;
			try {
				session = repository.login(credentials, wkspName);
			} catch (NoSuchWorkspaceException e) {
				if (createIfNeeded) {
					Session defaultSession = repository.login(credentials);
					try {
						defaultSession.getWorkspace().createWorkspace(wkspName);
					} catch (Exception e1) {
						throw new SlcException("Cannot create new workspace "
								+ wkspName, e);
					} finally {
						JcrUtils.logoutQuietly(defaultSession);
					}
					session = repository.login(credentials, wkspName);
				} else
					throw new SlcException("Workspace" + wkspName
							+ "does not exists and should not be created", e);
			}
			return session;
		} catch (RepositoryException e) {
			throw new SlcException("cannot create session" + " for workspace "
					+ path, e);
		} finally {
			JcrUtils.logoutQuietly(nodeSession);
		}
	}

	/*
	 * DEPENDENCY INJECTION
	 */
	public void setRepositoryFactory(RepositoryFactory repositoryFactory) {
		this.repositoryFactory = repositoryFactory;
	}

	public void setKeyring(Keyring keyring) {
		this.keyring = keyring;
	}

	public void setRepository(Repository nodeRepository) {
		this.nodeRepository = nodeRepository;
	}
}