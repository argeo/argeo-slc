package org.argeo.slc.akb.ui.utils;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

import org.argeo.slc.akb.AkbException;
import org.argeo.slc.akb.AkbTypes;
import org.argeo.slc.akb.ui.providers.ActiveTreeItem;
import org.argeo.slc.akb.utils.AkbJcrUtils;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

/** Enable specific sorting of the ResultTreeView */
public class AkbItemsComparator extends ViewerComparator {

	@Override
	public int category(Object element) {
		Node currNode = null;
		if (element instanceof ActiveTreeItem)
			currNode = ((ActiveTreeItem) element).getNode();
		else if (element instanceof Node)
			currNode = (Node) element;
		else if (element instanceof String)
			// TODO why do we have strings
			return 10;
		else
			throw new AkbException("Unsupported tree item element type "
					+ element);

		try {
			if (currNode.isNodeType(AkbTypes.AKB_ITEM_FOLDER))
				// folder always first
				return 5;
		} catch (RepositoryException e) {
			throw new AkbException("Cannot retrieve category for item", e);
		}
		return 10;
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		int cat1 = category(e1);
		int cat2 = category(e2);

		if (cat1 != cat2) {
			return cat1 - cat2;
		}

		// TODO we only check on one item, types are not mixed for the time
		// being
		Node an = null;
		Node bn = null;
		if (e1 instanceof ActiveTreeItem) {
			an = ((ActiveTreeItem) e1).getNode();
			bn = ((ActiveTreeItem) e2).getNode();
		} else if (e1 instanceof Node) {
			an = ((Node) e1);
			bn = ((Node) e2);
		} else
			throw new AkbException("Unsupported tree item element type " + e1);

		return super.compare(viewer, AkbJcrUtils.get(an, Property.JCR_TITLE),
				AkbJcrUtils.get(bn, Property.JCR_TITLE));
	}
}