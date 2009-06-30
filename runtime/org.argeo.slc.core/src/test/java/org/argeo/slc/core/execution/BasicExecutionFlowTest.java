package org.argeo.slc.core.execution;

import java.util.HashMap;
import java.util.Map;

import org.argeo.slc.core.test.SimpleTestResult;
import org.argeo.slc.execution.ExecutionContext;
import org.argeo.slc.execution.ExecutionFlow;
import org.argeo.slc.test.TestStatus;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ConfigurableApplicationContext;

public class BasicExecutionFlowTest extends AbstractExecutionFlowTestCase {
	// TO TEST
	// - post-processing for @{} replacement in beans with complex properties
	// - bean of scope other than execution are not resolved at execution

	// public void testMyTest() throws Exception {
	// ConfigurableApplicationContext applicationContext =
	// createApplicationContext("test.xml");
	// log.info("Start Execution");
	// ((ExecutionFlow) applicationContext.getBean("flow1")).execute();
	// applicationContext.close();
	// }

	public void testSpecOverriding() throws Exception {
		ConfigurableApplicationContext applicationContext = createApplicationContext("specOverriding.xml");
		((ExecutionFlow) applicationContext.getBean("flow2")).run();
		SimpleTestResult res = (SimpleTestResult) applicationContext
				.getBean("myTestResult");
		validateTestResult(res);
	}

	public void testMultipleFlows() throws Exception {
		ConfigurableApplicationContext applicationContext = createApplicationContext("multipleFlow.xml");
		((ExecutionFlow) applicationContext.getBean("flow1")).run();
		SimpleTestResult res = (SimpleTestResult) applicationContext
				.getBean("myTestResult");
		validateTestResult(res);
		res.getParts().clear();
		((ExecutionFlow) applicationContext.getBean("flow2")).run();
		validateTestResult(res, TestStatus.FAILED);
		applicationContext.close();
	}

	/**
	 * Test placeholder resolution in a context without scope execution or proxy
	 * and with cascading flows (the flow A contains the flow B)
	 * 
	 * @throws Exception
	 */
	public void testPlaceHolders() throws Exception {
		ConfigurableApplicationContext applicationContext = createApplicationContext("placeHolders.cascading.xml");
		((ExecutionFlow) applicationContext.getBean("flowA")).run();
		validateTestResult((SimpleTestResult) applicationContext
				.getBean("myTestResult"));
		applicationContext.close();
	}

	/**
	 * Test placeholder resolution in a context without scope execution or proxy
	 * and with cascading flows (the flow A contains the flow B) setting
	 * execution values (should have no effect)
	 * 
	 * @throws Exception
	 */
	public void testPlaceHoldersWithExecutionValues() throws Exception {
		ConfigurableApplicationContext applicationContext = createApplicationContext("placeHolders.cascading.xml");

		ExecutionContext executionContext = (ExecutionContext) applicationContext
				.getBean("executionContext");
		Map<String, String> executionParameters = new HashMap<String, String>();
		executionParameters.put("p1", "e1");
		executionParameters.put("p2", "e2");
		executionParameters.put("p3", "e3");
		executionParameters.put("p4", "e4");
		executionParameters.put("p5", "e5");
		executionParameters.put("p6", "e6");
		executionParameters.put("p7", "e7");
		executionParameters.put("p8", "e8");
		addVariables(executionContext, executionParameters);

		((ExecutionFlow) applicationContext.getBean("flowA")).run();
		validateTestResult((SimpleTestResult) applicationContext
				.getBean("myTestResult"));
		applicationContext.close();
	}

	public void testPlaceHoldersExec() throws Exception {
		ConfigurableApplicationContext applicationContext = createApplicationContext("placeHolders.cascading.exec.xml");

		ExecutionContext executionContext = (ExecutionContext) applicationContext
				.getBean("executionContext");
		Map<String, String> executionParameters = new HashMap<String, String>();
		executionParameters.put("p1", "e1");
		executionParameters.put("p2", "e2");
		executionParameters.put("p3", "e3");
		executionParameters.put("p4", "e4");
		executionParameters.put("p5", "e5");
		executionParameters.put("p6", "e6");
		addVariables(executionContext, executionParameters);

		((ExecutionFlow) applicationContext.getBean("flowA")).run();
		validateTestResult((SimpleTestResult) applicationContext
				.getBean("myTestResult"));
		applicationContext.close();
	}

	public void testCanonicFlowParameters() throws Exception {
		configureAndExecuteSlcFlow("canonic-001.xml", "canonic.001");
	}

	public void testCanonicDefaultValues() throws Exception {
		configureAndExecuteSlcFlow("canonic-002.xml", "canonic.002");
	}

	public void testCanonicMissingValues() throws Exception {
		try {
			configureAndExecuteSlcFlow("canonic-003.error.xml", "canonic.003");
			fail("Parameter not set - should be rejected.");
		} catch (BeanCreationException e) {
			// exception expected
			logException(e);
		}
	}

	public void testCanonicUnknownParameter() throws Exception {
		try {
			configureAndExecuteSlcFlow("canonic-004.error.xml", "canonic.004");
			fail("Unknown parameter set - should be rejected.");
		} catch (BeanCreationException e) {
			// exception expected
			logException(e);
		}
	}

	public void testListSetMap() throws Exception {
		ConfigurableApplicationContext applicationContext = createApplicationContext("listSetMap.xml");
		ExecutionFlow executionFlow = (ExecutionFlow) applicationContext
				.getBean("myFlow");
		executionFlow.run();

		validateTestResult((SimpleTestResult) applicationContext
				.getBean("myTestResult"));

		// BasicTestData res = (BasicTestData)
		// applicationContext.getBean("cascadingComplex.testData");
		// log.info("res=" + res.getReached().toString());

		applicationContext.close();
	}

	public void testListSetMapMultipleFlows() throws Exception {
		ConfigurableApplicationContext applicationContext = createApplicationContext("listSetMapMultipleFlow.xml");
		((ExecutionFlow) applicationContext.getBean("flow1")).run();
		SimpleTestResult res = (SimpleTestResult) applicationContext
				.getBean("myTestResult");
		validateTestResult(res);
		res.getParts().clear();
		((ExecutionFlow) applicationContext.getBean("flow2")).run();
		validateTestResult(res, TestStatus.FAILED);
		applicationContext.close();
	}

	protected void addVariables(ExecutionContext executionContext,
			Map<String, String> vars) {
		for (String key : vars.keySet())
			executionContext.setVariable(key, vars.get(key));
	}
}
