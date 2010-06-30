/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.argeo.slc.unit.test.tree;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.fail;
import static org.argeo.slc.unit.UnitUtils.assertDateSec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.core.structure.SimpleSElement;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.SimpleResultPart;
import org.argeo.slc.core.test.tree.PartSubList;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.test.TestResultPart;

/** Utilities for unit tests. */
public class UnitTestTreeUtil {
	private final static Log log = LogFactory.getLog(UnitTestTreeUtil.class);

	public static void assertTreeTestResult(TreeTestResult expected,
			TreeTestResult reached) {
		assertEquals(expected.getUuid(), reached.getUuid());
		assertDateSec(expected.getCloseDate(), reached.getCloseDate());

		// Attributes
		//assertEquals(expected.getAttributes().size(), reached.getAttributes()
		//		.size());
		for (String key : expected.getAttributes().keySet()) {
			String expectedValue = expected.getAttributes().get(key);
			String reachedValue = reached.getAttributes().get(key);
			assertNotNull(reachedValue);
			assertEquals(expectedValue, reachedValue);
		}

		// Result parts
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

		// Elements
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

	/** Asserts one part of a tree test result */
	public static void assertPart(TreeTestResult testResult, String pathStr,
			int index, Integer status, String message) {
		TreeSPath path = new TreeSPath(pathStr);
		PartSubList list = testResult.getResultParts().get(path);
		if (list == null) {
			fail("No result for path " + path);
			return;
		}
		if (index >= list.getParts().size()) {
			fail("Not enough parts.");
		}
		SimpleResultPart part = (SimpleResultPart) list.getParts().get(index);
		assertPart(part, status, message, null, part.getTestRunUuid(), true);
	}

	public static void assertPart(TestResultPart expected,
			TestResultPart reached) {
		String expectedTestRunUuid = null;
		if (expected instanceof SimpleResultPart) {
			expectedTestRunUuid = ((SimpleResultPart) expected)
					.getTestRunUuid();
		}

		assertPart(reached, expected.getStatus(), expected.getMessage(),
				expected.getExceptionMessage(), expectedTestRunUuid, false);
	}

	/** Assert one part of a tree test result. */
	private static void assertPart(TestResultPart part, Integer status,
			String message, String exceptionDescription,
			String expectedTestRunUuid, boolean skipExceptionMessage) {
		assertEquals(status, part.getStatus());

		if (message != null) {
			if (log.isTraceEnabled()) {
				log.trace("Expected message:" + message);
				log.trace("Reached message:" + part.getMessage());
			}
			assertEquals(message, part.getMessage());
		}

		if (!skipExceptionMessage) {
			if (exceptionDescription == null) {
				assertNull(part.getExceptionMessage());
			} else {
				if (log.isTraceEnabled()) {
					log.trace("Expected exception message:"
							+ exceptionDescription);
					log.trace("Reached exception message:"
							+ part.getExceptionMessage());
				}

				assertEquals(exceptionDescription, part.getExceptionMessage());
			}
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

	public static void describeTreeTestResult(TreeTestResult ttr) {
		log.info("TreeTestResult #" + ttr.getUuid());
		log.info(" Close date: " + ttr.getCloseDate());
		log.info(" Attributes:");
		for (String key : ttr.getAttributes().keySet())
			log.info("  " + key + "=" + ttr.getAttributes().get(key));

		log.info(" Result parts: (size=" + ttr.getResultParts().size() + ")");
		for (TreeSPath path : ttr.getResultParts().keySet()) {
			log.info(" Path: " + path);
			PartSubList lst = ttr.getResultParts().get(path);
			for (TestResultPart part : lst.getParts())
				log.info("  " + part);
		}

		log.info(" Elements: (size=" + ttr.getElements().size() + ")");
		for (TreeSPath path : ttr.getElements().keySet()) {
			SimpleSElement elem = (SimpleSElement) ttr.getElements().get(path);
			log.info(" Path: " + path + ", Element: " + elem.getLabel());
			for (String tag : elem.getTags().keySet())
				log.info("  " + tag + "=" + elem.getTags().get(tag));
		}

	}

	/** Makes sure this is a singleton */
	private UnitTestTreeUtil() {

	}
}
