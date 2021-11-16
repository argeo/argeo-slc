package org.argeo.slc.client.ui.providers;

import org.argeo.eclipse.ui.TreeParent;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/** Basic content provider for a tree of result */
public class ResultTreeContentProvider implements ITreeContentProvider {

	/**
	 * @param parent
	 *            Pass current user home as parameter
	 * 
	 */
	public Object[] getElements(Object parent) {
		if (parent instanceof Object[])
			return (Object[]) parent;
		else
			return null;
	}

	public Object getParent(Object child) {
		return ((TreeParent) child).getParent();
	}

	public Object[] getChildren(Object parent) {
		return ((TreeParent) parent).getChildren();
	}

	public boolean hasChildren(Object parent) {
		return ((TreeParent) parent).hasChildren();
	}

	public void dispose() {
		// FIXME implement if needed
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}