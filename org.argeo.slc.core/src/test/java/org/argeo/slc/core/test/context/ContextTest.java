package org.argeo.slc.core.test.context;

import org.argeo.slc.core.test.SimpleTestResult;
import org.argeo.slc.unit.AbstractSpringTestCase;

public class ContextTest extends AbstractSpringTestCase {

	public void testComplexContext() {
		SimpleTestResult testResult = new SimpleTestResult();
		ContextUtils.compareReachedExpected(
				(ContextAware) getBean("context.c1"), testResult, null);
		ContextUtils.compareReachedExpected(
				(ContextAware) getBean("context.c2"), testResult, null);
		ContextUtils.compareReachedExpected(
				(ContextAware) getBean("context.c3"), testResult, null);
	}
}
