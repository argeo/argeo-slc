package org.argeo.slc.client.ui.model;

import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.argeo.slc.SlcException;
import org.argeo.slc.SlcNames;
import org.argeo.slc.SlcTypes;

/**
 * UI Tree component. Virtual folder to list a list of results. Keeps a
 * reference to its parent that might be null. It also keeps a reference to all
 * nodes that must be displayed as children of the current virtual folder.
 */
public class VirtualFolder extends ResultParent {
	List<Node> displayedNodes;

	public VirtualFolder(VirtualFolder parent, List<Node> displayedNodes,
			String name) {
		super(name);
		setParent(parent);
		this.displayedNodes = displayedNodes;
	}

	@Override
	protected void initialize() {
		try {
			for (Node currNode : displayedNodes) {
				if (currNode.isNodeType(SlcTypes.SLC_TEST_RESULT)) {
					SingleResultNode srn = new SingleResultNode(this, currNode,
							currNode.getProperty(SlcNames.SLC_TEST_CASE)
									.getString());
					addChild(srn);
				}
			}
		} catch (RepositoryException re) {
			throw new SlcException(
					"Unexpected error while initializing ParentNodeFolder : "
							+ getName(), re);
		}
	}
}