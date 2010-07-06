/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
	
	// THis tests causes pb when using Spring 3
	public void testContainers() throws Exception {
		ConfigurableApplicationContext applicationContext = createApplicationContext("containers.xml");
		((ExecutionFlow) applicationContext.getBean("test.list.flow1")).run();
		((ExecutionFlow) applicationContext.getBean("test.list.flow2")).run();
		
		validateTestResult((SimpleTestResult) applicationContext
				.getBean("testResult"));			
	}
}
