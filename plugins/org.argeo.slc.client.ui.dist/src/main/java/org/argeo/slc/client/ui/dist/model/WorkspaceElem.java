package org.argeo.slc.client.ui.dist.model;

import javax.jcr.Credentials;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.argeo.eclipse.ui.TreeParent;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;

/** Abstracts a workspace that contains a given distribution */
public class WorkspaceElem extends TreeParent {
	private final RepoElem repoElem;
	private final Node workspaceNode;

	/**
	 * Helper to display only version when the workspace name is well formatted
	 */
	private static String formatName(Node workspaceNode) {
		String name = JcrUtils.getNameQuietly(workspaceNode);
		if (name != null && name.lastIndexOf('-') > 0)
			return name.substring(name.lastIndexOf('-') + 1);
		else
			return name;
	}

	public WorkspaceElem(RepoElem repoElem, Node workspaceNode) {
		super(formatName(workspaceNode));
		this.repoElem = repoElem;
		this.workspaceNode = workspaceNode;
	}

	public Node getWorkspaceNode() {
		return workspaceNode;
	}

	public String getWorkspaceName() {
		return JcrUtils.getNameQuietly(workspaceNode);
	}

	public String getWorkspacePath() {
		try {
			return workspaceNode.getPath();
		} catch (RepositoryException e) {
			throw new SlcException("Cannot get or add workspace path "
					+ getWorkspaceName(), e);
		}
	}

	public String getRepoPath() {
		try {
			return workspaceNode.getParent().getPath();
		} catch (RepositoryException e) {
			throw new SlcException("Cannot get or add workspace path "
					+ getWorkspaceName(), e);
		}
	}

	public RepoElem getRepoElem() {
		return repoElem;
	}

	public Credentials getCredentials() {
		return repoElem.getCredentials();
	}

	public boolean isReadOnly() {
		return repoElem.isReadOnly();
	}
}
