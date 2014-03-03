package org.argeo.slc.client.ui.dist.model;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;

import org.argeo.ArgeoException;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;

/** Abstract a workspace that contains a software distribution */
public class WorkspaceElem extends DistParentElem {
	private final RepoElem repoElem;
	private String workspaceName;
	private Session defaultSession;

	public WorkspaceElem(WkspGroupElem parent, RepoElem repoElem,
			String workspaceName) {
		super(workspaceName, repoElem.inHome(), repoElem.isReadOnly());
		this.repoElem = repoElem;
		this.workspaceName = workspaceName;
		setParent(parent);
	}

	public String getWorkspaceName() {
		return workspaceName;
	}

	public RepoElem getRepoElem() {
		return repoElem;
	}

	public Boolean isConnected() {
		if (defaultSession != null && defaultSession.isLive())
			return true;
		else
			return false;
	}

	public void login() {
		defaultSession = repoElem.repositoryLogin(getName());
	}

	public boolean hasChildren() {
		try {
			if (isConnected())
				return defaultSession.getRootNode().hasNodes();
			else
				return false;
		} catch (RepositoryException re) {
			throw new ArgeoException(
					"Unexpected error while checking children node existence",
					re);
		}
	}

	/** Override normal behaviour to initialize display of the workspace */
	@Override
	public synchronized Object[] getChildren() {
		if (isLoaded()) {
			return super.getChildren();
		} else {
			// initialize current object
			try {
				if (defaultSession == null)
					return null;
				else {
					Query groupQuery = defaultSession
							.getWorkspace()
							.getQueryManager()
							.createQuery(
									"select * from [" + SlcTypes.SLC_GROUP_BASE
											+ "] as group order by group.["
											+ SlcNames.SLC_GROUP_BASE_ID + "]",
									Query.JCR_SQL2);
					NodeIterator groups = groupQuery.execute().getNodes();
					while (groups.hasNext()) {
						addChild(new GroupBaseElem(WorkspaceElem.this, groups
								.nextNode()
								.getProperty(SlcNames.SLC_GROUP_BASE_ID)
								.getString()));
					}
				}
				return super.getChildren();
			} catch (RepositoryException e) {
				throw new ArgeoException(
						"Cannot initialize WorkspaceNode UI object."
								+ getName(), e);
			}
		}
	}
	
	@Override
	public synchronized void dispose() {
		JcrUtils.logoutQuietly(defaultSession);
		super.dispose();
	}
}
