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
	
	protected void validateTestResult(SimpleTestResult testResult, int expectedStatus) {
		for(TestResultPart part : testResult.getParts()) {
			if(part.getStatus() != expectedStatus) {
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
