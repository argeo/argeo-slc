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
package org.argeo.slc.core.execution;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.core.test.SimpleTestResult;
import org.argeo.slc.execution.ExecutionFlow;
import org.argeo.slc.test.TestResultPart;
import org.argeo.slc.test.TestStatus;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public abstract class AbstractExecutionFlowTestCase extends TestCase {

	protected final Log log = LogFactory.getLog(getClass());

	protected void logException(Throwable ex) {
		log.info("Got Exception of class " + ex.getClass().toString()
				+ " with message '" + ex.getMessage() + "'.");
	}

	protected void validateTestResult(SimpleTestResult testResult) {
		validateTestResult(testResult, TestStatus.PASSED);
	}

	protected void validateTestResult(SimpleTestResult testResult,
			int expectedStatus) {
		for (TestResultPart part : testResult.getParts()) {
			if (part.getStatus() != expectedStatus) {
				fail("Error found in TestResult: " + part.getMessage());
			}
		}
	}

	protected ConfigurableApplicationContext createApplicationContext(
			String applicationContextSuffix) {
		ConfigurableApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				inPackage(applicationContextSuffix));
		// applicationContext.start();
		return applicationContext;
	}

	protected void configureAndExecuteSlcFlow(String applicationContextSuffix,
			String beanName) {
		ConfigurableApplicationContext applicationContext = createApplicationContext(applicationContextSuffix);
		ExecutionFlow executionFlow = (ExecutionFlow) applicationContext
				.getBean(beanName);
		executionFlow.run();
		applicationContext.close();
	}

	protected String inPackage(String suffix) {
		String prefix = getClass().getPackage().getName().replace('.', '/');
		return prefix + '/' + suffix;
	}
}
