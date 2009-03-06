package org.argeo.slc.core.execution;

import org.argeo.slc.execution.ExecutionFlow;
import org.argeo.slc.unit.AbstractSpringTestCase;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ExecutionFlowTest extends AbstractSpringTestCase {
	
	public void testSimpleExecution() throws Exception {
//		configureAndExecuteSlcFlow("main");
		configureAndExecuteSlcFlow("applicationContext.xml", "main");
	}
	
	public void testCanonic() throws Exception {
		configureAndExecuteSlcFlow("minimal.xml", "minimal");
		// Parameter without default value in specification
		configureAndExecuteSlcFlow("canonic-001.xml", "canonic.001");
//		configureAndExecuteSlcFlow("canonic-002.xml", "canonic.002");

/*		try {
			configureAndExecuteSlcFlow("canonic-003.error.xml", "canonic.003");
			fail("Parameter not set - should be rejected.");
		} catch (BeanCreationException e) {
			// exception expected
			//e.printStackTrace();
		}*/
		
/*		try {
			configureAndExecuteSlcFlow("canonic-004.error.xml", "canonic.004");
			fail("Unkown parameter set - should be rejected.");
		} catch (BeanCreationException e) {
			// exception expected
			//e.printStackTrace();
		}		*/
	}	
	
	protected void configureSlcFlow(String beanName) {
		// if an execution context was registered, unregister it
		if(ExecutionContext.getCurrent() != null) {
			ExecutionContext.unregisterExecutionContext();
		}
		// register a new ExecutionContext
		ExecutionContext.registerExecutionContext(new ExecutionContext());
		
//		ExecutionContext.getVariables().put("slc.flows", beanName);
	}
	
/*	
	@Override
	protected Boolean getIsStartContext() {
		return true;
	}

	@Override
	protected ConfigurableApplicationContext getContext() {
		return getStaticContext();
	}

	private static ConfigurableApplicationContext staticContext;	
		
	protected ConfigurableApplicationContext getStaticContext() {
		if (staticContext == null) {
			staticContext = new ClassPathXmlApplicationContext(
					getApplicationContextLocation());
			if(getIsStartContext())
				staticContext.start();
		}
		return staticContext;		
	}
	
	protected void configureAndExecuteSlcFlow(String beanName) {
		// Triggers a start of the ApplicationContext
		// Required before starting some tests
		// TODO: understand why !
		getContext(); 
		configureSlcFlow(beanName);
		ExecutionFlow executionFlow = (ExecutionFlow) getContext().getBean(beanName);
		executionFlow.execute();		
	}
	*/
	
	protected void configureAndExecuteSlcFlow(String applicationContextSuffix, String beanName) {
		// create a new context
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				inPackage(applicationContextSuffix));
		applicationContext.start();
		
		configureSlcFlow(beanName);
		ExecutionFlow executionFlow = (ExecutionFlow) applicationContext.getBean(beanName);
		executionFlow.execute();		
//		applicationContext.stop();
		applicationContext.close();
	}	
}
