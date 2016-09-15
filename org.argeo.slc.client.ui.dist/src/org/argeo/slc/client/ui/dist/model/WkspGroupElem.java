package org.argeo.slc.client.ui.dist.model;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;

/**
 * Abstract set of similar workspaces, that is a bunch of workspaces with same
 * prefix.
 */
public class WkspGroupElem extends DistParentElem {

	private Session defaultSession;

	public WkspGroupElem(RepoElem repoElem, String prefix) {
		super(prefix, repoElem.inHome(), repoElem.isReadOnly());
		setParent(repoElem);
		// Directly adds children upon creation
		try {
			defaultSession = repoElem.repositoryLogin(null);
			String[] wkpNames = defaultSession.getWorkspace()
					.getAccessibleWorkspaceNames();
			for (String wkpName : wkpNames) {
				if (prefix.equals(getPrefix(wkpName))
				// if (wkpName.startsWith(prefix)
						&& repoElem.isWorkspaceVisible(wkpName))
					addChild(new WorkspaceElem(WkspGroupElem.this, repoElem,
							wkpName));
			}
		} catch (RepositoryException e) {
			throw new SlcException("Cannot retrieve workspace names", e);
		}
	}

	// FIXME - we rely on a "hard coded" convention : Workspace name must have
	// this format: name-major.minor
	// We might expose this method as static public, to be used among others by
	// the RepoElem parent objects when building its children
	private String getPrefix(String workspaceName) {
		if (workspaceName.lastIndexOf(VERSION_SEP) > 0)
			return workspaceName.substring(0,
					workspaceName.lastIndexOf(VERSION_SEP));
		else
			return workspaceName;
	}

	public void dispose() {
		JcrUtils.logoutQuietly(defaultSession);
		super.dispose();
	}
}