package org.argeo.slc.client.ui.dist.model;

import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;

/**
 * Abstracts a group of distribution, that is a bunch of workspaces with same
 * prefix.
 */
public class GroupElem extends DistParentElem {
	private RepoElem repoElem;
	private String name;

	public GroupElem(RepoElem repoElem, String prefix) {
		super(repoElem.inHome(), repoElem.isReadOnly());
		this.repoElem = repoElem;
		this.name = prefix;
	}

	public Object[] getChildren() {
		Session session = null;
		try {
			Repository repository = repoElem.getRepository();
			session = repository.login(repoElem.getCredentials());

			String[] workspaceNames = session.getWorkspace()
					.getAccessibleWorkspaceNames();
			List<WorkspaceElem> distributionElems = new ArrayList<WorkspaceElem>();
			buildWksp: for (String workspaceName : workspaceNames) {

				// Filter non-public workspaces for user anonymous.
				if (repoElem.getRepoNode() == null) {
					Session tmpSession = null;
					try {
						tmpSession = repository.login(workspaceName);
						Boolean res = true;
						try {
							tmpSession.checkPermission("/", "read");
						} catch (AccessControlException e) {
							res = false;
						}
						if (!res)
							continue buildWksp;
					} catch (RepositoryException e) {
						throw new SlcException(
								"Cannot list workspaces for anonymous user", e);
					} finally {
						JcrUtils.logoutQuietly(tmpSession);
					}
				}

				// filter technical workspaces
				if (workspaceName.startsWith(name)) {
					distributionElems.add(new WorkspaceElem(repoElem,
							workspaceName));
				}
			}
			return distributionElems.toArray();
		} catch (RepositoryException e) {
			throw new SlcException("Cannot list workspaces for prefix " + name,
					e);
		} finally {
			JcrUtils.logoutQuietly(session);
		}
	}

	public String getLabel() {
		return name;
	}

	public String toString() {
		return getLabel();
	}

	public void dispose() {
	}

	public RepoElem getRepoElem() {
		return repoElem;
	}

}
