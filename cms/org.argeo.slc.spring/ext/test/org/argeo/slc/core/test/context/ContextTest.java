package org.argeo.slc.core.test.context;

import java.util.List;

import org.argeo.slc.runtime.test.ContextUtils;
import org.argeo.slc.runtime.test.SimpleTestResult;
import org.argeo.slc.test.TestResultPart;
import org.argeo.slc.test.TestStatus;
import org.argeo.slc.test.context.ContextAware;

public class ContextTest extends AbstractInternalSpringTestCase {

	public void testComplexContext() {
		SimpleTestResult testResult = new SimpleTestResult();
		ContextUtils.compareReachedExpected(
				(ContextAware) getBean("context.c1"), testResult);
		ContextUtils.compareReachedExpected(
				(ContextAware) getBean("context.c2"), testResult);
		ContextUtils.compareReachedExpected(
				(ContextAware) getBean("context.c3"), testResult);

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
