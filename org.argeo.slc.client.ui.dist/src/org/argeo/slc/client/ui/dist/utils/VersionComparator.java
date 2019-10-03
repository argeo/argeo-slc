package org.argeo.slc.client.ui.dist.utils;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

/**
 * Enable comparison of two version string with form "1.2.5.qualifier" with
 * following rules and assumptions:
 * <ul>
 * <li>
 * Version are parsed and compared segment by segment; doing best effort to
 * convert major, minor and micro to integer and compare them as such (to have
 * 0.1 < 0.9 < 0.10 not 0.1 < 0.10 < 0.9).</li>
 * <li>Version should not contain any dash (-), version segments should be
 * separated by dots (.)</li>
 * </ul>
 */

public class VersionComparator extends ViewerComparator {
	private static final long serialVersionUID = 3760077835650538982L;

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		String s1 = (String) e1;
		String s2 = (String) e2;
		return compareVersion(s1, s2);
	}

	/**
	 * Enable comparison of two versions of the form
	 * "major.minor.micro.qualifier". We assume the separator is always a "."
	 * and make best effort to convert major, minor and micro to int.
	 */
	private int compareVersion(String v1, String v2) {
		String[] t1 = v1.split("\\.");
		String[] t2 = v2.split("\\.");

		for (int i = 0; i < t1.length && i < t2.length; i++) {
			int result = compareToken(t1[i], t2[i]);
			if (result != 0)
				return result;
		}
		if (t1.length > t2.length)
			return 1;
		else if (t1.length < t2.length)
			return -1;
		else
			return 0;
	}

	private int compareToken(String t1, String t2) {
		if (t1 == null && t2 == null)
			return 0;
		else if (t1 == null)
			return -1;
		else if (t2 == null)
			return 1;

		Integer i1 = null, i2 = null;
		try {
			i1 = new Integer(t1);
			i2 = new Integer(t2);
		} catch (NumberFormatException nfe) {
			// the format is not valid we silently compare as String
			return t1.compareTo(t2);
		}
		return i1.compareTo(i2);
	}
}