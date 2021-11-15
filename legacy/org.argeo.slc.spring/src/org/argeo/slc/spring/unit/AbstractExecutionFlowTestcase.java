package org.argeo.slc.spring.unit;

import org.argeo.slc.execution.ExecutionFlow;

public abstract class AbstractExecutionFlowTestcase extends AbstractSpringTestCase {
	@SuppressWarnings(value = { "unchecked" })
	protected <T extends ExecutionFlow> T executeFlow(String flowName) {
		ExecutionFlow flow = getBean(flowName);
		flow.run();
		return (T) flow;
	}
}
