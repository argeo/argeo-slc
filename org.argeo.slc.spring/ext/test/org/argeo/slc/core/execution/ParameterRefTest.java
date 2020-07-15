package org.argeo.slc.core.execution;

import org.argeo.slc.execution.ExecutionFlow;
import org.argeo.slc.runtime.test.SimpleTestResult;
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
