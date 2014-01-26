package org.argeo.slc.client.ui.dist.model;

/** Abstracts a workspace that contains a given distribution */
public class WorkspaceElem extends DistParentElem {
	private final RepoElem repoElem;
	private String workspaceName;
	private String label;

	/**
	 * Helper to display only version when the workspace name is well formatted
	 */
	private static String formatName(String name) {
		if (name != null && name.lastIndexOf(VERSION_SEP) > 0)
			return name.substring(name.lastIndexOf(VERSION_SEP) + 1);
		else
			return name;
	}

	public WorkspaceElem(RepoElem repoElem, String workspaceName) {
		this.repoElem = repoElem;
		this.workspaceName = workspaceName;
		this.label = formatName(workspaceName);
	}

	public String getWorkspaceName() {
		return workspaceName;
	}

	public RepoElem getRepoElem() {
		return repoElem;
	}

	public boolean isReadOnly() {
		return repoElem.isReadOnly();
	}

	public boolean hasChildren() {
		return false;
	}

	public Object[] getChildren() {
		return null;
	}

	@Override
	public String getLabel() {
		return label;
	}
}
