package org.argeo.slc.core.execution.xml;

import org.argeo.slc.core.execution.AbstractExecutionFlowTestCase;
import org.argeo.slc.execution.ExecutionContext;
import org.argeo.slc.execution.ExecutionFlow;
import org.argeo.slc.runtime.test.SimpleTestResult;
import org.springframework.context.ConfigurableApplicationContext;

public abstract class FlowNamespaceTest extends AbstractExecutionFlowTestCase {
	public void XXXtestCanonical() throws Exception {
		ConfigurableApplicationContext applicationContext = createApplicationContext("canonic-ns.xml");
		((ExecutionFlow) applicationContext.getBean("canonic-ns.001")).run();
		((ExecutionFlow) applicationContext.getBean("canonic-ns.002")).run();
	}

	public void XXXtestAdvanced() throws Exception {
		ConfigurableApplicationContext applicationContext = createApplicationContext("advanced.xml");
		((ExecutionFlow) applicationContext.getBean("flow1")).run();
		((ExecutionFlow) applicationContext.getBean("flow2")).run();
		((ExecutionFlow) applicationContext.getBean("flow3")).run();

		validateTestResult((SimpleTestResult) applicationContext
				.getBean("testResult"));
	}

	public void XXXtestAdvancedExecution() throws Exception {
		ConfigurableApplicationContext applicationContext = createApplicationContext("advanced.xml");

		ExecutionContext executionContext = (ExecutionContext) applicationContext
				.getBean("executionContext");
		executionContext.setVariable("param2", 4);

		((ExecutionFlow) applicationContext.getBean("flow4")).run();

		validateTestResult((SimpleTestResult) applicationContext
				.getBean("testResult"));
	}

	// These tests causes pb when using Spring 3
	
	// public void testContainers() throws Exception {
	// ConfigurableApplicationContext applicationContext =
	// createApplicationContext("containers.xml");
	// ((ExecutionFlow) applicationContext.getBean("test.list.flow1")).run();
	// ((ExecutionFlow) applicationContext.getBean("test.list.flow2")).run();
	//
	// validateTestResult((SimpleTestResult) applicationContext
	// .getBean("testResult"));
	// }
}
