package org.argeo.slc.core.execution;

import org.argeo.slc.core.test.SimpleTestResult;
import org.argeo.slc.execution.ExecutionFlow;
import org.argeo.slc.test.TestStatus;
import org.argeo.slc.unit.AbstractSpringTestCase;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ExecutionFlowTest extends AbstractSpringTestCase {
	
	public void testSimpleExecution() throws Exception {
		configureAndExecuteSlcFlow("applicationContext.xml", "main");
	}
	
	public void testCanonic() throws Exception {
		// Parameter without default value in specification
/*		configureAndExecuteSlcFlow("canonic-001.xml", "canonic.001");
		configureAndExecuteSlcFlow("canonic-002.xml", "canonic.002");

		try {
			configureAndExecuteSlcFlow("canonic-003.error.xml", "canonic.003");
			fail("Parameter not set - should be rejected.");
		} catch (BeanCreationException e) {
			// exception expected
			logException(e);
		}*/
		
/*		try {
			configureAndExecuteSlcFlow("canonic-004.error.xml", "canonic.004");
			fail("Unknown parameter set - should be rejected.");
		} catch (BeanCreationException e) {
			// exception expected
			logException(e);
		}	*/
	}	
	
/*	public void testRecursive() throws Exception {
		ConfigurableApplicationContext applicationContext = prepareExecution("test.xml");
		ExecutionFlow executionFlow = (ExecutionFlow) applicationContext.getBean("first");
		executionFlow.execute();		
		SimpleTestResult res = (SimpleTestResult) applicationContext.getBean("basicTestResult");
		if(res.getParts().get(0).getStatus() != TestStatus.PASSED) {
			fail("Unexpected string returned");
		}
		applicationContext.close();		
	}*/
	
	protected void logException(Throwable ex) {
		log.info("Got Exception of class " + ex.getClass().toString()
				+ " with message '" + ex.getMessage() + "'.");
	}
	
	protected void initExecutionContext() {
/*		// if an execution context was registered, unregister it
		if(MapExecutionContext.getCurrent() != null) {
			MapExecutionContext.unregisterExecutionContext();
		}
		// register a new ExecutionContext
		MapExecutionContext.registerExecutionContext(new MapExecutionContext());		*/
	}
	
	protected ConfigurableApplicationContext prepareExecution(String applicationContextSuffix) {
		ConfigurableApplicationContext applicationContext = new ClassPathXmlApplicationContext(inPackage(applicationContextSuffix));
		applicationContext.start();
		initExecutionContext();
		return applicationContext;
	}
	
	protected void configureAndExecuteSlcFlow(String applicationContextSuffix, String beanName) {
		ConfigurableApplicationContext applicationContext = prepareExecution(applicationContextSuffix);
		ExecutionFlow executionFlow = (ExecutionFlow) applicationContext.getBean(beanName);
		executionFlow.execute();		
		applicationContext.close();
	}	
}
