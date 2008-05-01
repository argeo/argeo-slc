package org.argeo.slc.unit.test.tree;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.fail;
import static org.argeo.slc.unit.UnitUtils.assertDateSec;

import org.argeo.slc.core.structure.SimpleSElement;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.SimpleResultPart;
import org.argeo.slc.core.test.TestResultPart;
import org.argeo.slc.core.test.tree.PartSubList;
import org.argeo.slc.core.test.tree.TreeTestResult;

/** Utilities for unit tests. */
public class UnitTestTreeUtil {
	public static void assertTreeTestResult(TreeTestResult expected,
			TreeTestResult reached) {
		assertEquals(expected.getUuid(), reached.getUuid());
		assertDateSec(expected.getCloseDate(), reached.getCloseDate());

		assertEquals(expected.getResultParts().size(), reached.getResultParts()
				.size());
		for (TreeSPath path : expected.getResultParts().keySet()) {
			PartSubList lstExpected = expected.getResultParts().get(path);
			PartSubList lstReached = expected.getResultParts().get(path);
			if (lstReached == null) {
				fail("No result for path " + path);
				return;
			}
			assertPartSubList(lstExpected, lstReached);
		}

		assertEquals(expected.getElements().size(), reached.getElements()
				.size());
		for (TreeSPath path : expected.getElements().keySet()) {
			// String nameExpected = expected.getElements().get(path);
			// String nameReached = expected.getElements().get(path);
			SimpleSElement elemExpected = (SimpleSElement) expected
					.getElements().get(path);
			SimpleSElement elemReached = (SimpleSElement) expected
					.getElements().get(path);
			assertNotNull(elemReached);
			assertElements(elemExpected, elemReached);
		}

	}

	public static void assertElements(SimpleSElement expected,
			SimpleSElement reached) {
		assertEquals(expected.getLabel(), reached.getLabel());
		assertEquals(expected.getTags().size(), reached.getTags().size());
		for (String tagName : expected.getTags().keySet()) {
			String expectedTagValue = expected.getTags().get(tagName);
			String reachedTagValue = reached.getTags().get(tagName);
			assertNotNull(reachedTagValue);
			assertEquals(expectedTagValue, reachedTagValue);
		}
	}

	public static void assertPartSubList(PartSubList lstExpected,
			PartSubList lstReached) {
		assertEquals(lstExpected.getParts().size(), lstReached.getParts()
				.size());
		for (int i = 0; i < lstExpected.getParts().size(); i++) {
			assertPart(lstExpected.getParts().get(i), lstReached.getParts()
					.get(i));
		}
	}

	/**
	 * Assert one part of a tree test result.
	 * 
	 * @deprecated use {@link #assertPart(TestResultPart, TestResultPart)}
	 *             instead
	 */
	public static void assertPart(TreeTestResult testResult, String pathStr,
			int index, Integer status, String message) {
		TreeSPath path = TreeSPath.parseToCreatePath(pathStr);
		PartSubList list = testResult.getResultParts().get(path);
		if (list == null) {
			fail("No result for path " + path);
			return;
		}
		if (index >= list.getParts().size()) {
			fail("Not enough parts.");
		}
		SimpleResultPart part = (SimpleResultPart) list.getParts().get(index);
		assertPart(part, status, message, null, null, part.getTestRunUuid());
	}

	public static void assertPart(TestResultPart expected,
			TestResultPart reached) {
		String expectedTestRunUuid = null;
		if (expected instanceof SimpleResultPart) {
			expectedTestRunUuid = ((SimpleResultPart) expected)
					.getTestRunUuid();
		}

		assertPart(reached, expected.getStatus(), expected.getMessage(),
				expected.getExceptionMessage(), expected
						.getExceptionStackLines(), expectedTestRunUuid);
	}

	/** Assert one part of a tree test result. */
	private static void assertPart(TestResultPart part, Integer status,
			String message, String exceptionDescription,
			List<String> stackLines, String expectedTestRunUuid) {
		assertEquals(status, part.getStatus());
		assertEquals(message, part.getMessage());
		if (exceptionDescription == null) {
			assertNull(part.getExceptionMessage());
		} else {
			assertEquals(exceptionDescription, part.getExceptionMessage());
			assertEquals(stackLines.size(), part.getExceptionStackLines()
					.size());
		}

		if (expectedTestRunUuid != null) {
			SimpleResultPart reachedPart = (SimpleResultPart) part;
			assertNotNull(reachedPart.getTestRunUuid());
			assertEquals(expectedTestRunUuid, reachedPart.getTestRunUuid());
		} else {
			if (part instanceof SimpleResultPart) {
				assertNull(((SimpleResultPart) part).getTestRunUuid());
			}

		}

	}

	/** Makes sure this is a singleton */
	private UnitTestTreeUtil() {

	}
}
