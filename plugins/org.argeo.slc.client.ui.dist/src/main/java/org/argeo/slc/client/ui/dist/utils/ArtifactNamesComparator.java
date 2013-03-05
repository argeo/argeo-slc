package org.argeo.slc.client.ui.dist.utils;

import org.argeo.eclipse.ui.TreeParent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

/**
 * Enable comparison of two names with form org.argeo.slc-1.2.x
 */

public class ArtifactNamesComparator extends ViewerComparator {

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
		if (i1 <0)
			if (i2 <0)
				return s1.compareTo(s2);
			else 
				return 1;
		else 
			if (i2 <0)
				return -1;

		String aPref = s1.substring(0, s1.lastIndexOf('-'));
		String aSuf = s1.substring(s1.lastIndexOf('-'));

		String bPref = s2.substring(0, s2.lastIndexOf('-'));
		String bSuf = s2.substring(s2.lastIndexOf('-'));

		result = aPref.compareTo(bPref);
		if (result != 0)
			return result;
		else
			return bSuf.compareTo(aSuf);

	}
}