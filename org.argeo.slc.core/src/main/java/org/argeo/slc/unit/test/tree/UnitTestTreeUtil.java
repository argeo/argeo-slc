package org.argeo.slc.unit.test.tree;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.fail;

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
		assertEquals(expected.getTestResultId(), reached.getTestResultId());
		assertEquals(expected.getCloseDate(), reached.getCloseDate());

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
		if (lstExpected.getSlcExecutionUuid() == null) {
			assertNull(lstReached.getSlcExecutionUuid());
		} else {
			assertEquals(lstExpected.getSlcExecutionUuid(), lstReached
					.getSlcExecutionUuid());
		}

		if (lstExpected.getSlcExecutionStepUuid() == null) {
			assertNull(lstReached.getSlcExecutionStepUuid());
		} else {
			assertEquals(lstExpected.getSlcExecutionStepUuid(), lstReached
					.getSlcExecutionStepUuid());
		}

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
		assertPart(part, status, message, null);
	}

	public static void assertPart(TestResultPart expected,
			TestResultPart reached) {
		assertPart(reached, expected.getStatus(), expected.getMessage(),
				expected.getException());
	}

	/** Assert one part of a tree test result. */
	private static void assertPart(TestResultPart part, Integer status,
			String message, Exception exception) {
		assertEquals(status, part.getStatus());
		assertEquals(message, part.getMessage());
		if (exception == null) {
			assertNull(part.getException());
		} else {
			assertEquals(exception, part.getException());
		}
	}

	/** Makes sure this is a singleton */
	private UnitTestTreeUtil() {

	}
}
