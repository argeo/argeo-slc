package org.argeo.slc.client.ui.dist.model;

/**
 * Abstract a node of type slc:groupBase that gathers a set of artifacts that
 * have the same group ID
 */
public class GroupBaseElem extends DistParentElem {
	// private WorkspaceElem wkspElem;
	private String groupId;

	public GroupBaseElem(WorkspaceElem wkspElem, String groupId) {
		super(groupId, wkspElem.inHome(), wkspElem.isReadOnly());

		setParent(wkspElem);
		// this.wkspElem = wkspElem;
		this.groupId = groupId;
	}

	public Object[] getChildren() {
		return null;
	}

	public String getLabel() {
		return groupId;
	}

	// public String toString() {
	// return getLabel();
	// }

	// public void dispose() {
	// }

	public WorkspaceElem getWorkspaceElem() {
		return (WorkspaceElem) getParent();
	}

	public String getGroupId() {
		return getName();
	}
}