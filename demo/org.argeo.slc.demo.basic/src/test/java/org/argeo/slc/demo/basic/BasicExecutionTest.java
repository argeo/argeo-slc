package org.argeo.slc.demo.basic;

import java.util.Map;

import junit.framework.TestCase;

import org.argeo.slc.core.execution.ExecutionContext;
import org.argeo.slc.execution.ExecutionFlow;
import org.springframework.beans.factory.generic.GenericBeanFactoryAccessor;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BasicExecutionTest extends TestCase {
	public void testExecution() throws Exception {
		String[] files = { "conf/imports.xml", "conf/common.xml",
				"conf/basic.xml", "conf/canonic.xml",
				"conf/testCases/basic-001.xml", "conf/testCases/basic-002.xml",
				"conf/testCases/canonic-001.xml",
				"conf/testCases/canonic-002.xml", "conf/main.xml" };
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				files);
		applicationContext.start();

		// GenericBeanFactoryAccessor accessor = new
		// GenericBeanFactoryAccessor(applicationContext);
		// Map<String, Execut>

		String bean = "main";
		ExecutionContext.registerExecutionContext(new ExecutionContext());
		ExecutionContext.getVariables().put("slc.flows", bean);
		ExecutionFlow executionFlow = (ExecutionFlow) applicationContext
				.getBean(bean);
		executionFlow.execute();

		// SlcExecution slcExecution = new SlcExecution();
		// slcExecution.getAttributes().put("slc.flows", "main");
		// applicationContext.publishEvent(new NewExecutionEvent(this,
		// slcExecution));
		//		 
		// Thread.sleep(5000);

		applicationContext.close();
	}
}
