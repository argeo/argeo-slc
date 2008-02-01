package org.argeo.slc.unit.test.tree;

import junit.framework.TestCase;

import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.SimpleResultPart;
import org.argeo.slc.core.test.tree.PartSubList;
import org.argeo.slc.core.test.tree.TreeTestResult;

/** Utilities for unit tests. */
public class UnitTestTreeUtil {
	/** Assert one part of a tree test result. */
	public static void assertPart(TreeTestResult testResult, String pathStr,
			int index, Integer status, String message) {
		TreeSPath path = TreeSPath.parseToCreatePath(pathStr);
		PartSubList list = testResult.getResultParts().get(path);
		if (list == null) {
			TestCase.fail("No result for path " + path);
			return;
		}
		SimpleResultPart part = (SimpleResultPart) list.getParts().get(index);
		TestCase.assertEquals(status, part.getStatus());
		TestCase.assertEquals(message, part.getMessage());
	}

	/** Makes sure this is a singleton */
	private UnitTestTreeUtil() {

	}
}
