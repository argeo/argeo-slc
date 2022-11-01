package org.argeo.cms.ui.jcr.model;

import javax.jcr.AccessDeniedException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
// import javax.jcr.Workspace;
import javax.jcr.Workspace;

import org.argeo.cms.ux.widgets.TreeParent;
import org.argeo.eclipse.ui.EclipseUiException;
import org.argeo.jcr.JcrUtils;

/**
 * UI Tree component. Wraps the root node of a JCR {@link Workspace}. It also
 * keeps a reference to its parent {@link RepositoryElem}, to be able to
 * retrieve alias of the current used repository
 */
public class WorkspaceElem extends TreeParent {
	private Session session = null;

	public WorkspaceElem(RepositoryElem parent, String name) {
		this(parent, name, null);
	}

	public WorkspaceElem(RepositoryElem parent, String name, Session session) {
		super(name);
		this.session = session;
		setParent(parent);
	}

	public synchronized Session getSession() {
		return session;
	}

	public synchronized Node getRootNode() {
		try {
			if (session != null)
				return session.getRootNode();
			else
				return null;
		} catch (RepositoryException e) {
			throw new EclipseUiException("Cannot get root node of workspace " + getName(), e);
		}
	}

	public synchronized void login() {
		try {
			session = ((RepositoryElem) getParent()).repositoryLogin(getName());
		} catch (RepositoryException e) {
			throw new EclipseUiException("Cannot connect to repository " + getName(), e);
		}
	}

	public Boolean isConnected() {
		if (session != null && session.isLive())
			return true;
		else
			return false;
	}

	@Override
	public synchronized void dispose() {
		logout();
		super.dispose();
	}

	/** Logouts the session, does not nothing if there is no live session. */
	public synchronized void logout() {
		clearChildren();
		JcrUtils.logoutQuietly(session);
		session = null;
	}

	@Override
	public synchronized boolean hasChildren() {
		try {
			if (isConnected())
				try {
					return session.getRootNode().hasNodes();
				} catch (AccessDeniedException e) {
					// current user may not have access to the root node
					return false;
				}
			else
				return false;
		} catch (RepositoryException re) {
			throw new EclipseUiException("Unexpected error while checking children node existence", re);
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
				Node rootNode;
				if (session == null)
					return null;
				else
					rootNode = session.getRootNode();
				NodeIterator ni = rootNode.getNodes();
				while (ni.hasNext()) {
					Node node = ni.nextNode();
					addChild(new SingleJcrNodeElem(this, node, node.getName()));
				}
				return super.getChildren();
			} catch (RepositoryException e) {
				throw new EclipseUiException("Cannot initialize WorkspaceNode UI object." + getName(), e);
			}
		}
	}
}
