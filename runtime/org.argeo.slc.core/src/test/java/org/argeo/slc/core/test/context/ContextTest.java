package org.argeo.slc.core.test.context;

import java.util.List;

import org.argeo.slc.core.test.SimpleTestResult;
import org.argeo.slc.test.TestResultPart;
import org.argeo.slc.test.TestStatus;
import org.argeo.slc.test.context.ContextAware;
import org.argeo.slc.unit.internal.AbstractSpringTestCase;

public class ContextTest extends AbstractSpringTestCase {

	public void testComplexContext() {
		SimpleTestResult testResult = new SimpleTestResult();
		ContextUtils.compareReachedExpected(
				(ContextAware) getBean("context.c1"), testResult, null);
		ContextUtils.compareReachedExpected(
				(ContextAware) getBean("context.c2"), testResult, null);
		ContextUtils.compareReachedExpected(
				(ContextAware) getBean("context.c3"), testResult, null);

		List<TestResultPart> parts = testResult.getParts();
		assertEquals(6, parts.size());
		assertEquals(TestStatus.PASSED, parts.get(0).getStatus());
		assertEquals(TestStatus.PASSED, parts.get(1).getStatus());
		assertEquals(TestStatus.PASSED, parts.get(2).getStatus());
		assertEquals(TestStatus.FAILED, parts.get(3).getStatus());
		assertEquals(TestStatus.PASSED, parts.get(4).getStatus());
		assertEquals(TestStatus.PASSED, parts.get(5).getStatus());
	}
}
