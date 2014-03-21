package org.argeo.slc.client.ui.dist;

import javax.jcr.Repository;
import javax.jcr.RepositoryFactory;
import javax.jcr.Session;

import org.argeo.slc.repo.RepoUtils;
import org.argeo.util.security.Keyring;

public class RepoServiceImpl implements RepoService {

	/* DEPENDENCY INJECTION */
	private Repository nodeRepository;
	private RepositoryFactory repositoryFactory;
	private Keyring keyring;

	public Session getRemoteSession(String repoNodePath, String uri,
			String workspaceName) {
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
