package org.argeo.slc.core.test;

import org.argeo.slc.SlcException;
import org.argeo.slc.core.structure.tree.TreeSRelatedHelper;
import org.argeo.slc.core.test.context.ContextUtils;
import org.argeo.slc.test.IncompatibleTestDataException;
import org.argeo.slc.test.TestData;
import org.argeo.slc.test.TestDefinition;
import org.argeo.slc.test.TestResult;
import org.argeo.slc.test.TestRun;
import org.argeo.slc.test.TestStatus;
import org.argeo.slc.test.context.ContextAware;

/** Understands basic test data and context aware test data. */
public class BasicTestDefinition extends TreeSRelatedHelper implements
		TestDefinition {

	public void execute(TestRun testRun) {
		if (testRun.<TestData> getTestData() instanceof BasicTestData) {
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
		} else if (testRun.<TestData> getTestData() instanceof ContextAware) {
			TestData testData = testRun.getTestData();
			ContextUtils.compareReachedExpected((ContextAware) testData,
					testRun.getTestResult(), this);
		} else {
			throw new IncompatibleTestDataException(testRun);
		}
	}
}
