/*
 * Copyright (C) 2007-2012 Mathieu Baudier
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
package org.argeo.slc.core.test;

import org.argeo.slc.SlcException;
import org.argeo.slc.core.test.context.ContextUtils;
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
