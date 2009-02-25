package org.argeo.slc.demo.basic;

import junit.framework.TestCase;

import org.argeo.slc.core.execution.ExecutionContext;
import org.argeo.slc.core.execution.NewExecutionEvent;
import org.argeo.slc.execution.ExecutionFlow;
import org.argeo.slc.process.SlcExecution;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BasicExecutionTest extends TestCase {
	public void testExecution() throws Exception {
		String[] files = { "conf/main.xml", "conf/imports.xml",
				"conf/common.xml", "conf/basic.xml",
				"conf/testCases/basic-001.xml", "conf/testCases/basic-002.xml" };
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				files);
		applicationContext.start();

		String bean = "main";
		ExecutionContext.registerExecutionContext(new ExecutionContext());
		ExecutionContext.getVariables().put("slc.flows", bean);
		ExecutionFlow executionFlow = (ExecutionFlow) applicationContext
				.getBean(bean);
		executionFlow.execute();

//		 SlcExecution slcExecution = new SlcExecution();
//		 slcExecution.getAttributes().put("slc.flows", "main");
//		 applicationContext.publishEvent(new NewExecutionEvent(this,
//		 slcExecution));
//		 
//		 Thread.sleep(5000);

		applicationContext.close();
	}
}
