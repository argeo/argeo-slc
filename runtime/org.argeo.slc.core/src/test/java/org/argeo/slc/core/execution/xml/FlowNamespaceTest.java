package org.argeo.slc.core.execution.xml;

import org.argeo.slc.core.execution.AbstractExecutionFlowTestCase;
import org.argeo.slc.core.test.SimpleTestResult;
import org.argeo.slc.execution.ExecutionContext;
import org.argeo.slc.execution.ExecutionFlow;
import org.springframework.context.ConfigurableApplicationContext;

public class FlowNamespaceTest extends AbstractExecutionFlowTestCase {
	public void testCanonical() throws Exception {
		ConfigurableApplicationContext applicationContext = createApplicationContext("canonic-ns.xml");
		((ExecutionFlow) applicationContext.getBean("canonic-ns.001")).run();
		((ExecutionFlow) applicationContext.getBean("canonic-ns.002")).run();
	}
	
	public void testAdvanced() throws Exception {
		ConfigurableApplicationContext applicationContext = createApplicationContext("advanced.xml");
		((ExecutionFlow) applicationContext.getBean("flow1")).run();
		((ExecutionFlow) applicationContext.getBean("flow2")).run();
		((ExecutionFlow) applicationContext.getBean("flow3")).run();
		
		validateTestResult((SimpleTestResult) applicationContext
				.getBean("testResult"));		
	}	
	
	public void testAdvancedExecution() throws Exception {
		ConfigurableApplicationContext applicationContext = createApplicationContext("advanced.xml");
		
		ExecutionContext executionContext = (ExecutionContext) applicationContext
		.getBean("executionContext");
		executionContext.setVariable("param2", 4);
		
		((ExecutionFlow) applicationContext.getBean("flow4")).run();
		
		validateTestResult((SimpleTestResult) applicationContext
				.getBean("testResult"));		
	}	
	
	public void testContainers() throws Exception {
		ConfigurableApplicationContext applicationContext = createApplicationContext("containers.xml");
		((ExecutionFlow) applicationContext.getBean("test.list.flow1")).run();
		((ExecutionFlow) applicationContext.getBean("test.list.flow2")).run();
		
		validateTestResult((SimpleTestResult) applicationContext
				.getBean("testResult"));			
	}
}
