package org.argeo.slc.repo.core;

import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.RepositoryFactory;
import javax.jcr.Session;

import org.argeo.jcr.JcrUtils;
import org.argeo.slc.repo.RepoService;
import org.argeo.slc.repo.RepoUtils;
import org.argeo.util.security.Keyring;

/**
 * Work in Progress - enhance this. First implementation of a service that
 * centralizes session management in an argeo SLC context, repositories are
 * either defined using an URI and a workspace name in a anonymous context or
 * using connection information that are store in a corresponding node in the
 * local repository home
 */
public class RepoServiceImpl implements RepoService {

	/* DEPENDENCY INJECTION */
	private Repository nodeRepository;
	private RepositoryFactory repositoryFactory;
	private Keyring keyring;

	public Session getRemoteSession(String repoNodePath, String uri,
			String workspaceName) {

		// TODO remove this. Only usefull while investigating the RAP login
		// problem
		Session session = null;
		try {
			session = nodeRepository.login();
		} catch (LoginException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			JcrUtils.logoutQuietly(session);
		}

		return RepoUtils.getRemoteSession(repositoryFactory, keyring,
				nodeRepository, repoNodePath, uri, workspaceName);
	}

	/* DEPENDENCY INJECTION */
	public void setNodeRepository(Repository nodeRepository) {
		this.nodeRepository = nodeRepository;
	}

	public void setRepositoryFactory(RepositoryFactory repositoryFactory) {
		this.repositoryFactory = repositoryFactory;
	}

	public void setKeyring(Keyring keyring) {
		this.keyring = keyring;
	}
}