package org.argeo.slc.client.ui.dist.providers;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.argeo.ArgeoException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ArtifactsTreeContentProvider implements ITreeContentProvider {
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
				elements = nodesList.toArray();
			}
		} catch (RepositoryException e) {
			throw new ArgeoException(
					"Unexpected exception while listing node properties", e);
		}
		return elements;
	}

	public boolean hasChildren(Object parent) {
		try {
			if (parent instanceof Node && ((Node) parent).hasNodes()) {
				return true;
			}
		} catch (RepositoryException e) {
			throw new ArgeoException(
					"Unexpected exception while checking if property is multiple",
					e);
		}
		return false;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	public void dispose() {
	}
}