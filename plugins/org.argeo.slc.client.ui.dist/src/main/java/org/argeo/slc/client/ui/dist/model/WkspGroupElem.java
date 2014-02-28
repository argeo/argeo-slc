package org.argeo.slc.client.ui.dist.model;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.argeo.ArgeoException;
import org.argeo.jcr.JcrUtils;

/**
 * Abstract set of similar workspaces, that is a bunch of workspaces with same
 * prefix.
 */
public class WkspGroupElem extends DistParentElem {

	private Session defaultSession;

	/**
	 */
	public WkspGroupElem(RepoElem repoElem, String prefix) {
		super(prefix, repoElem.inHome(), repoElem.isReadOnly());
		setParent(repoElem);
		// Directly adds children upon creation
		try {
			defaultSession = repoElem.repositoryLogin(null);
			String[] wkpNames = defaultSession.getWorkspace()
					.getAccessibleWorkspaceNames();
			for (String wkpName : wkpNames) {
				if (wkpName.startsWith(prefix)
						&& repoElem.isWorkspaceVisible(wkpName))
					addChild(new WorkspaceElem(WkspGroupElem.this, repoElem,
							wkpName));
			}
		} catch (RepositoryException e) {
			throw new ArgeoException("Cannot retrieve workspace names", e);
		}
	}

	//
	// public Object[] getChildren() {
	// Session session = null;
	// try {
	// Repository repository = repoElem.getRepository();
	// session = repository.login(repoElem.getCredentials());
	//
	// String[] workspaceNames = session.getWorkspace()
	// .getAccessibleWorkspaceNames();
	// List<WorkspaceElem> distributionElems = new ArrayList<WorkspaceElem>();
	// buildWksp: for (String workspaceName : workspaceNames) {
	//
	// // Filter non-public workspaces for user anonymous.
	// if (repoElem.getRepoNode() == null) {
	// Session tmpSession = null;
	// try {
	// tmpSession = repository.login(workspaceName);
	// Boolean res = true;
	// try {
	// tmpSession.checkPermission("/", "read");
	// } catch (AccessControlException e) {
	// res = false;
	// }
	// if (!res)
	// continue buildWksp;
	// } catch (RepositoryException e) {
	// throw new SlcException(
	// "Cannot list workspaces for anonymous user", e);
	// } finally {
	// JcrUtils.logoutQuietly(tmpSession);
	// }
	// }
	//
	// // filter technical workspaces
	// if (workspaceName.startsWith(name)
	// && workspaceName.substring(0,
	// workspaceName.lastIndexOf(VERSION_SEP)).equals(
	// name)) {
	// distributionElems.add(new WorkspaceElem(repoElem,
	// workspaceName));
	// }
	// }
	// return distributionElems.toArray();
	// } catch (RepositoryException e) {
	// throw new SlcException("Cannot list workspaces for prefix " + name,
	// e);
	// } finally {
	// JcrUtils.logoutQuietly(session);
	// }
	// }

	// public String getLabel() {
	// return name;
	// }
	//
	// public String toString() {
	// return getLabel();
	// }

	public void dispose() {
		JcrUtils.logoutQuietly(defaultSession);
		super.dispose();
	}

	// public RepoElem getRepoElem() {
	// return repoElem;
	// }
}