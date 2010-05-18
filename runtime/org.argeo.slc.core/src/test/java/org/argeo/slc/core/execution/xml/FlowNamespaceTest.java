package org.argeo.slc.core.execution.xml;

import org.argeo.slc.core.execution.AbstractExecutionFlowTestCase;
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
	}	
}
