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

package org.argeo.slc.core.execution;

import org.argeo.slc.core.test.SimpleTestResult;
import org.argeo.slc.execution.ExecutionFlow;
import org.argeo.slc.test.TestStatus;
import org.springframework.context.ConfigurableApplicationContext;

public class ParameterRefTest extends AbstractExecutionFlowTestCase {
	public void test001() throws Exception {
		ConfigurableApplicationContext applicationContext = createApplicationContext("parameterRef.xml");
		((ExecutionFlow) applicationContext.getBean("parameterRef.001")).run();

		SimpleTestResult res = (SimpleTestResult) applicationContext
				.getBean("parameterRef.testResult");
		assertEquals(res.getParts().get(0).getStatus(), TestStatus.PASSED);
		assertEquals(res.getParts().get(1).getStatus(), TestStatus.FAILED);

		applicationContext.close();
	}

}
