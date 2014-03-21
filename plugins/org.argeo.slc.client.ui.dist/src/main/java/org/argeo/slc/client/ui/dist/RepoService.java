package org.argeo.slc.client.ui.dist;

import javax.jcr.Session;

/** Start factorisation of the session management using a manager service */
public interface RepoService {

	/**
	 * Returns a corresponding session given the current context. Caller must
	 * close the session once it as been used
	 */
	public Session getRemoteSession(String repoNodePath, String uri,
			String workspaceName);
}
