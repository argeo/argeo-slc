package org.argeo.slc.client.ui.dist.utils;

import org.argeo.eclipse.ui.TreeParent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

/**
 * Enable comparison of two names version string with form org.argeo.slc-1.2.x.
 * with following rules and assumptions:
 * <ul>
 * <li>
 * Names are ordered using Lexicographical order</li>
 * <li>
 * Version are parsed and compared segment by segment; doing best effort to
 * convert major, minor and micro to integer and compare them as such (to have
 * 0.1 < 0.9 < 0.10 not 0.1 < 0.10 < 0.9).</li>
 * <li>Version should not contain any dash (-), version segments should be
 * separated by dots (.)</li>
 * </ul>
 */

public class NameVersionComparator extends ViewerComparator {

	private VersionComparator vc = new VersionComparator();

	@Override
	public int category(Object element) {
		if (element instanceof String) {
			int lastInd = ((String) element).lastIndexOf('-');
			if (lastInd > 0)
				return 10;
		}
		// unvalid names always last
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

		String s1, s2;

		if (e1 instanceof TreeParent) {
			s1 = ((TreeParent) e1).getName();
			s2 = ((TreeParent) e2).getName();
		} else {
			s1 = e1.toString();
			s2 = e2.toString();
		}

		int i1 = s1.lastIndexOf('-');
		int i2 = s2.lastIndexOf('-');

		// Specific cases, unvalid Strings
		if (i1 < 0)
			if (i2 < 0)
				return s1.compareTo(s2);
			else
				return 1;
		else if (i2 < 0)
			return -1;

		String aName = s1.substring(0, s1.lastIndexOf('-'));
		String aVersion = s1.substring(s1.lastIndexOf('-'));

		String bName = s2.substring(0, s2.lastIndexOf('-'));
		String bVersion = s2.substring(s2.lastIndexOf('-'));

		result = aName.compareTo(bName);
		if (result != 0)
			return result;
		else
			return vc.compare(viewer, aVersion, bVersion);
	}
}