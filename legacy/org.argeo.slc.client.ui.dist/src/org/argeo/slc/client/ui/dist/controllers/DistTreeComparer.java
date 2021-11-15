package org.argeo.slc.client.ui.dist.controllers;

import org.argeo.eclipse.ui.TreeParent;
import org.eclipse.jface.viewers.IElementComparer;

/** Compares two elements of the Distribution tree */
public class DistTreeComparer implements IElementComparer {

	public int hashCode(Object element) {
		if (element instanceof TreeParent)
			return ((TreeParent) element).hashCode();
		else

			return element.getClass().toString().hashCode();
	}

	public boolean equals(Object elementA, Object elementB) {
		if (!(elementA instanceof TreeParent)
				|| !(elementB instanceof TreeParent)) {
			return elementA == null ? elementB == null : elementA
					.equals(elementB);
		} else {
			TreeParent tpA = ((TreeParent) elementA);
			TreeParent tpB = ((TreeParent) elementB);
			return tpA.compareTo(tpB) == 0;
		}
	}
}