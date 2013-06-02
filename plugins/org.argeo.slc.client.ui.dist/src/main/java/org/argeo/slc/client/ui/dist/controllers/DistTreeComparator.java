package org.argeo.slc.client.ui.dist.controllers;

import org.argeo.slc.client.ui.dist.model.DistParentElem;
import org.argeo.slc.client.ui.dist.model.GroupElem;
import org.argeo.slc.client.ui.dist.model.RepoElem;
import org.argeo.slc.client.ui.dist.model.WorkspaceElem;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

/** Specific behaviour to enhence Distribution tree browsers */
public class DistTreeComparator extends ViewerComparator {

	public int category(Object element) {
		if (element instanceof RepoElem)
			if (((RepoElem) element).inHome())
				// Home repository always first
				return 2;
			else
				return 5;
		else if (element instanceof GroupElem)
			return 10;
		else if (element instanceof WorkspaceElem)
			return 15;
		else
			return 20;
	}

	public int compare(Viewer viewer, Object e1, Object e2) {
		int cat1 = category(e1);
		int cat2 = category(e2);

		if (cat1 != cat2) {
			return cat1 - cat2;
		}

		String s1, s2;

		if (e1 instanceof DistParentElem) {
			s1 = ((DistParentElem) e1).getLabel();
			s2 = ((DistParentElem) e2).getLabel();
		} else {
			s1 = e1.toString();
			s2 = e2.toString();
		}

		if (e1 instanceof WorkspaceElem)
			// Reverse order for versions
			return s2.compareTo(s1);
		else
			return s1.compareTo(s2);
	}
}