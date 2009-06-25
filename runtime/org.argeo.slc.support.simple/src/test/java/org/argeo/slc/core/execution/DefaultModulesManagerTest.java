package org.argeo.slc.core.execution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.argeo.slc.SlcException;
import org.argeo.slc.execution.ExecutionFlowDescriptor;
import org.argeo.slc.execution.ExecutionModule;
import org.argeo.slc.process.RealizedFlow;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.unit.AbstractSpringTestCase;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import junit.framework.TestCase;

public class DefaultModulesManagerTest {//extends AbstractSpringTestCase {
/*
	public void testSimpleExecution() throws Exception {
		//do nothing
		
		// create an execution
		SlcExecution execution = new SlcExecution();
		execution.setUuid("TestUUID");
		List<RealizedFlow> realizedFlows = new ArrayList<RealizedFlow>();
		RealizedFlow flow = new RealizedFlow();
		flow.setModuleName("dummyname");
		flow.setModuleVersion("dummyversion");
		ExecutionFlowDescriptor executionFlowDescriptor = new ExecutionFlowDescriptor();
		executionFlowDescriptor.setName("main");
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("testKey", new PrimitiveValue(
				PrimitiveSpecAttribute.TYPE_INTEGER, 22));
		executionFlowDescriptor.setValues(values);
		flow.setFlowDescriptor(executionFlowDescriptor);
		realizedFlows.add(flow);
		execution.setRealizedFlows(realizedFlows);
		
		// create a module
		ApplicationContext applicationContext = prepareExecution("applicationContext.xml");
//		ExecutionModule module = createExecutionModule(applicationContext);
		ExecutionModule module = (ExecutionModule) applicationContext.getBean("executionModule_1");
		
		// create an Execution Module Manager
		DefaultModulesManager manager = new DefaultModulesManager();
		List<ExecutionModule> modules = new ArrayList<ExecutionModule>();
		modules.add(module);
		manager.setExecutionModules(modules);
		
		manager.process(execution);
	}	
	
	protected ExecutionModule createExecutionModule(ApplicationContext applicationContext) {
		AbstractSpringExecutionModule module = new AbstractSpringExecutionModule() {
			public String getName() {return "dummyname";}
			public String getVersion() {return "dummyversion";}			
		};
		module.setExecutionContext(new MapExecutionContext());
		module.setApplicationContext(applicationContext);
		return module;
	}
	
	protected ConfigurableApplicationContext prepareExecution(String applicationContextSuffix) {
		ConfigurableApplicationContext applicationContext = new ClassPathXmlApplicationContext(inPackage(applicationContextSuffix));
		applicationContext.start();
		return applicationContext;
	}	
	*/
}
