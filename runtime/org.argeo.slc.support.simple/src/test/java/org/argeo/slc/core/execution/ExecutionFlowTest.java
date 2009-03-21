package org.argeo.slc.core.execution;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.core.test.BasicTestData;
import org.argeo.slc.core.test.SimpleTestResult;
import org.argeo.slc.execution.ExecutionContext;
import org.argeo.slc.execution.ExecutionFlow;
import org.argeo.slc.test.TestResultPart;
import org.argeo.slc.test.TestStatus;
import org.argeo.slc.unit.AbstractSpringTestCase;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ExecutionFlowTest extends TestCase {
	
	protected final Log log = LogFactory.getLog(getClass());
		
	/**
	 * Test placeholder resolution in a context without scope execution or proxy
	 * and with cascading flows (the flow A contains the flow B)
	 * @throws Exception
	 */
	public void testPlaceHolders() throws Exception {
		ConfigurableApplicationContext applicationContext = createApplicationContext("placeHolders.cascading.xml");
		((ExecutionFlow) applicationContext.getBean("flowA")).execute();
		validateTestResult((SimpleTestResult) applicationContext.getBean("myTestResult"));
		applicationContext.close();
	}	
	
	/**
	 * Test placeholder resolution in a context without scope execution or proxy
	 * and with cascading flows (the flow A contains the flow B)
	 * setting execution values (should have no effect)
	 * @throws Exception
	 */
	public void testPlaceHoldersWithExecutionValues() throws Exception {
		ConfigurableApplicationContext applicationContext = createApplicationContext("placeHolders.cascading.xml");
		
		ExecutionContext executionContext = (ExecutionContext)applicationContext.getBean("executionContext");
		Map<String, String> executionParameters = new HashMap<String,String>();
		executionParameters.put("p1", "e1");
		executionParameters.put("p2", "e2");
		executionParameters.put("p3", "e3");
		executionParameters.put("p4", "e4");
		executionParameters.put("p5", "e5");
		executionParameters.put("p6", "e6");
		executionParameters.put("p7", "e7");
		executionParameters.put("p8", "e8");
		executionContext.addVariables(executionParameters);
		
		((ExecutionFlow) applicationContext.getBean("flowA")).execute();
		validateTestResult((SimpleTestResult) applicationContext.getBean("myTestResult"));
		applicationContext.close();
	}		
	
	public void testPlaceHoldersExec() throws Exception {
		ConfigurableApplicationContext applicationContext = createApplicationContext("placeHolders.cascading.exec.xml");
		
		ExecutionContext executionContext = (ExecutionContext)applicationContext.getBean("executionContext");
		Map<String, String> executionParameters = new HashMap<String,String>();
		executionParameters.put("p1", "e1");
		executionParameters.put("p2", "e2");
		executionParameters.put("p3", "e3");
		executionParameters.put("p4", "e4");
		executionParameters.put("p5", "e5");
		executionParameters.put("p6", "e6");
		executionContext.addVariables(executionParameters);
		
		((ExecutionFlow) applicationContext.getBean("flowA")).execute();
		validateTestResult((SimpleTestResult) applicationContext.getBean("myTestResult"));
		applicationContext.close();
	}		
	
	public void testCanonicFlowParameters()  throws Exception {
		configureAndExecuteSlcFlow("canonic-001.xml", "canonic.001");
	}

	public void testCanonicDefaultValues()  throws Exception {
		configureAndExecuteSlcFlow("canonic-002.xml", "canonic.002");
	}
	
	public void testCanonicMissingValues()  throws Exception {
		try {
			configureAndExecuteSlcFlow("canonic-003.error.xml", "canonic.003");
			fail("Parameter not set - should be rejected.");
		} catch (BeanCreationException e) {
			// exception expected
			logException(e);
		}	}	
	
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
		ExecutionFlow executionFlow = (ExecutionFlow) applicationContext.getBean("myFlow");
		executionFlow.execute();		
		
		validateTestResult((SimpleTestResult) applicationContext.getBean("myTestResult"));
		
		BasicTestData res = (BasicTestData) applicationContext.getBean("cascadingComplex.testData");
		log.info("res=" + res.getReached().toString());
		
		applicationContext.close();		
	}		


	protected void logException(Throwable ex) {
		log.info("Got Exception of class " + ex.getClass().toString()
				+ " with message '" + ex.getMessage() + "'.");
	}
		
	protected void validateTestResult(SimpleTestResult testResult) {
		for(TestResultPart part : testResult.getParts()) {
			if(part.getStatus() != TestStatus.PASSED) {
				fail("Error found in TestResult: " + part.getMessage());
			}
		}		
	}
	
	protected ConfigurableApplicationContext createApplicationContext(String applicationContextSuffix) {
		ConfigurableApplicationContext applicationContext = new ClassPathXmlApplicationContext(inPackage(applicationContextSuffix));
		applicationContext.start();
		return applicationContext;
	}
	
	protected void configureAndExecuteSlcFlow(String applicationContextSuffix, String beanName) {
		ConfigurableApplicationContext applicationContext = createApplicationContext(applicationContextSuffix);
		ExecutionFlow executionFlow = (ExecutionFlow) applicationContext.getBean(beanName);
		executionFlow.execute();		
		applicationContext.close();
	}	
	
	protected String inPackage(String suffix) {
		String prefix = getClass().getPackage().getName().replace('.', '/');
		return prefix + '/' + suffix;
	}	
}
