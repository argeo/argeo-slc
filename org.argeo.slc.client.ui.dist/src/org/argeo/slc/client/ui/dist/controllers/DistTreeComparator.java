package org.argeo.slc.client.ui.dist.controllers;

import org.argeo.slc.client.ui.dist.model.DistParentElem;
import org.argeo.slc.client.ui.dist.model.ModularDistVersionElem;
import org.argeo.slc.client.ui.dist.model.RepoElem;
import org.argeo.slc.client.ui.dist.model.WkspGroupElem;
import org.argeo.slc.client.ui.dist.model.WorkspaceElem;
import org.argeo.slc.client.ui.dist.utils.NameVersionComparator;
import org.argeo.slc.client.ui.dist.utils.VersionComparator;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

/** Specific comparator to enhance Distribution tree browsers */
public class DistTreeComparator extends ViewerComparator {
	private static final long serialVersionUID = -7386716562202568704L;

	private VersionComparator vc = new VersionComparator();
	private NameVersionComparator nvc = new NameVersionComparator();

	public int category(Object element) {
		if (element instanceof RepoElem)
			if (((RepoElem) element).inHome())
				// Home repository always first
				return 2;
			else
				return 5;
		else if (element instanceof WkspGroupElem)
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
			s1 = ((DistParentElem) e1).getName();
			s2 = ((DistParentElem) e2).getName();
		} else {
			s1 = e1.toString();
			s2 = e2.toString();
		}

		if (e1 instanceof WorkspaceElem)
			// Reverse order for nameversions
			return nvc.compare(viewer, s2, s1);
		else if (e1 instanceof ModularDistVersionElem)
			// Reverse order for versions
			return vc.compare(viewer, s2, s1);
		else
			return s1.compareTo(s2);
	}
}