package org.argeo.slc.runtime.test;

import org.argeo.slc.SlcException;
import org.argeo.slc.test.IncompatibleTestDataException;
import org.argeo.slc.test.TestData;
import org.argeo.slc.test.TestDefinition;
import org.argeo.slc.test.TestResult;
import org.argeo.slc.test.TestRun;
import org.argeo.slc.test.TestStatus;
import org.argeo.slc.test.context.ContextAware;

/** Understands basic test data and context aware test data. */
public class BasicTestDefinition implements TestDefinition {

	public void execute(TestRun testRun) {
		if (testRun.<TestData> getTestData() instanceof BasicTestData) {
			BasicTestData testData = testRun.getTestData();
			TestResult result = testRun.getTestResult();

			if (result == null)
				throw new SlcException("No test result defined.");

			try {
				if (testData.getExpected().equals(testData.getReached())) {
					result.addResultPart(new SimpleResultPart(
							TestStatus.PASSED, "Reached and expected equals"));
				} else {
					result.addResultPart(new SimpleResultPart(
							TestStatus.FAILED, "Expected "
									+ testData.getExpected() + " but reached "
									+ testData.getReached()));
				}
			} catch (Exception e) {
				result.addResultPart(new SimpleResultPart(TestStatus.ERROR,
						"Could not compare", e));
			}
		} else if (testRun.<TestData> getTestData() instanceof ContextAware) {
			TestData testData = testRun.getTestData();
			ContextUtils.compareReachedExpected((ContextAware) testData,
					testRun.getTestResult());
		} else {
			throw new IncompatibleTestDataException(testRun);
		}
	}
}
