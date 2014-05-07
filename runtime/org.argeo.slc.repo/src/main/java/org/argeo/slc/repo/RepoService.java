package org.argeo.slc.repo;

import javax.jcr.Session;

/** Start factorisation of the session management using a manager service */
public interface RepoService {

	/**
	 * Returns a corresponding session given the current context. Caller must
	 * close the session once it has been used
	 */
	public Session getRemoteSession(String repoNodePath, String uri,
			String workspaceName);
}
