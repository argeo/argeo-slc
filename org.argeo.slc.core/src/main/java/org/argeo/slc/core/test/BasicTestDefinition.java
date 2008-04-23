package org.argeo.slc.core.test;

import org.argeo.slc.core.structure.tree.TreeSRelatedHelper;
import org.argeo.slc.core.test.context.ContextAware;
import org.argeo.slc.core.test.context.ContextUtils;

/** Understands basic test data and context aware test data. */
public class BasicTestDefinition extends TreeSRelatedHelper implements
		TestDefinition {

	public void execute(TestRun testRun) {
		if (testRun.getTestData() instanceof BasicTestData) {
			BasicTestData testData = testRun.getTestData();
			TestResult result = testRun.getTestResult();

			try {
				if (testData.getExpected().equals(testData.getReached())) {
					result.addResultPart(new SimpleResultPart(
							TestStatus.PASSED, "Reached and expected equals"));
				} else {
					result.addResultPart(new SimpleResultPart(
							TestStatus.FAILED, "Expected "
									+ testData.getExpected() + " but reched "
									+ testData.getReached()));
				}
			} catch (Exception e) {
				result.addResultPart(new SimpleResultPart(TestStatus.ERROR,
						"Could not compare", e));
			}
		} else if (testRun.getTestData() instanceof ContextAware) {
			TestData testData = testRun.getTestData();
			ContextUtils.compareReachedExpected((ContextAware) testData,
					testRun.getTestResult(), this);
		}
	}
}
