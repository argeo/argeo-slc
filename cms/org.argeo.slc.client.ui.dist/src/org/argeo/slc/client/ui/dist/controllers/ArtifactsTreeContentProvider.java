package org.argeo.slc.client.ui.dist.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.argeo.eclipse.ui.jcr.util.JcrItemsComparator;
import org.argeo.slc.SlcException;
import org.argeo.slc.SlcTypes;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/** Enable specific browsing of an artifact tree */
public class ArtifactsTreeContentProvider implements ITreeContentProvider,
		SlcTypes {
	private static final long serialVersionUID = -8097817288192073987L;

	// Utils
	private boolean sortChildren = true;
	private JcrItemsComparator itemComparator = new JcrItemsComparator();

	public Object[] getElements(Object parent) {
		return getChildren(parent);
	}

	public Object getParent(Object child) {
		return null;
	}

	public Object[] getChildren(Object parent) {
		Object[] elements = null;
		try {
			if (parent instanceof Node) {
				Node node = (Node) parent;
				NodeIterator ni = node.getNodes();
				List<Node> nodesList = new ArrayList<Node>();
				while (ni.hasNext()) {
					nodesList.add(ni.nextNode());
				}
				if (sortChildren) {
					Node[] arr = (Node[]) nodesList.toArray(new Node[nodesList
							.size()]);
					Arrays.sort(arr, itemComparator);
					return arr;
				} else
					return nodesList.toArray();

			}
		} catch (RepositoryException e) {
			throw new SlcException(
					"Unexpected exception while listing node properties", e);
		}
		return elements;
	}

	public boolean hasChildren(Object parent) {
		try {
			if (parent instanceof Node) {
				Node curNode = (Node) parent;
				// We manually stop digging at this level
				if (curNode.isNodeType(SLC_ARTIFACT_VERSION_BASE))
					return false;
				else if (curNode.hasNodes())
					return true;
			}
		} catch (RepositoryException e) {
			throw new SlcException(
					"Unexpected exception while checking if property is multiple",
					e);
		}
		return false;
	}

	public void setSortChildren(boolean sortChildren) {
		this.sortChildren = sortChildren;
	}

	public boolean getSortChildren() {
		return sortChildren;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	public void dispose() {
	}
}