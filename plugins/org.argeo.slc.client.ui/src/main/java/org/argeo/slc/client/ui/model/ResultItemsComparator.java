package org.argeo.slc.client.ui.model;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

import org.argeo.eclipse.ui.TreeParent;
import org.argeo.slc.SlcException;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

/** Enable specific sorting of the ResultTreeView */
public class ResultItemsComparator extends ViewerComparator {

	@Override
	public int category(Object element) {
		if (element instanceof SingleResultNode) {
			return 10;

		}
		// folder always first
		return 5;
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		int cat1 = category(e1);
		int cat2 = category(e2);

		if (cat1 != cat2) {
			return cat1 - cat2;
		}

		int result = 0;

		if (e1 instanceof TreeParent && ((TreeParent) e1).getParent() == null) {
			// preserve predefined order on UI root items
			return 0;
		}

		if (e1 instanceof SingleResultNode && e2 instanceof SingleResultNode) {
			Node an = ((SingleResultNode) e1).getNode();
			Node bn = ((SingleResultNode) e2).getNode();
			try {
				// Order is different if we are under my Result or )in the
				// rest of the tree structure
				if (an.getParent().isNodeType(
						SlcTypes.SLC_MY_RESULT_ROOT_FOLDER)
						|| an.getParent()
								.isNodeType(SlcTypes.SLC_RESULT_FOLDER)) {
					result = super.compare(viewer, e1, e2);
					// Specific case of two result with same name
					if (result == 0) {
						result = an
								.getProperty(SlcNames.SLC_COMPLETED)
								.getDate()
								.compareTo(
										bn.getProperty(SlcNames.SLC_COMPLETED)
												.getDate());
					}
				} else {
					result = an
							.getProperty(Property.JCR_CREATED)
							.getDate()
							.compareTo(
									bn.getProperty(Property.JCR_CREATED)
											.getDate());
					result = result * -1; // last are displayed first
				}
			} catch (RepositoryException e) {
				throw new SlcException("Unable to compare date created", e);
			}
		} else
			// only remaining objects for the time being
			// NT_UNSTRUCTURED that display all result tree structures
			// We want the newest folders first
			result = super.compare(viewer, e1, e2) * -1;
		return result;
	}
}
