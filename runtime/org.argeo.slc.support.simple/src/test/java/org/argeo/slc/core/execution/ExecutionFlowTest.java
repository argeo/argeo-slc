package org.argeo.slc.core.execution;

import org.argeo.slc.core.test.SimpleTestResult;
import org.argeo.slc.execution.ExecutionFlow;
import org.argeo.slc.test.TestStatus;
import org.argeo.slc.unit.AbstractSpringTestCase;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ExecutionFlowTest extends AbstractSpringTestCase {
	
	public void testSimpleExecution() throws Exception {
//		prepareExecution("applicationContext.xml");
		configureAndExecuteSlcFlow("applicationContext.xml", "main_2");
		
//		ConfigurableApplicationContext applicationContext = new ClassPathXmlApplicationContext(inPackage("applicationContext.xml"));
//		String[] beanNames = applicationContext.getBeanDefinitionNames();
//		for(String beanName : beanNames) {
//			log.info("beanName='" + beanName + "'");
////			if(beanName.equals("main_temp")) {
//				BeanDefinition beanDef = applicationContext.getBeanFactory().getBeanDefinition(beanName);
//				String scope = beanDef.getScope();
//				String[] attrs = beanDef.();
//				log.info("scope=" + scope + ", nbAttr=" + attrs.length);
//				for(String attr : attrs) {
//					log.info(attr + "=" + beanDef.getAttribute(attr));
//				}
////			}
//		}
		
	}
	
	public void testCanonic() throws Exception {
		// Parameter without default value in specification
//		configureAndExecuteSlcFlow("canonic-001.xml", "canonic.001");
//		configureAndExecuteSlcFlow("canonic-002.xml", "canonic.002");
//
//		try {
//			prepareExecution("canonic-003.error.xml");
//			fail("Parameter not set - should be rejected.");
//		} catch (BeanCreationException e) {
//			// exception expected
//			logException(e);
//		}
		
/*		try {
			configureAndExecuteSlcFlow("canonic-004.error.xml", "canonic.004");
			fail("Unknown parameter set - should be rejected.");
		} catch (BeanCreationException e) {
			// exception expected
			logException(e);
		}	*/
	}	
	
	public void testRecursive() throws Exception {
//		ConfigurableApplicationContext applicationContext = prepareExecution("test.xml");
//		ExecutionFlow executionFlow = (ExecutionFlow) applicationContext.getBean("first");
//		executionFlow.execute();		
//		SimpleTestResult res = (SimpleTestResult) applicationContext.getBean("basicTestResult");
//		if(res.getParts().get(0).getStatus() != TestStatus.PASSED) {
//			fail("Unexpected string returned");
//		}
//		applicationContext.close();		
	}
	
	public void testCreateRef() throws Exception {
//		configureAndExecuteSlcFlow("createRef.xml", "basic.001");
	}
	
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
