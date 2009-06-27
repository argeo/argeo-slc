package org.argeo.slc.unit.execution;

import org.argeo.slc.execution.ExecutionFlow;
import org.argeo.slc.unit.AbstractSpringTestCase;

public class AbstractExecutionFlowTestcase extends AbstractSpringTestCase {
	@SuppressWarnings(value = { "unchecked" })
	protected <T extends ExecutionFlow> T executeFlow(String flowName) {
		ExecutionFlow flow = getBean(flowName);
		flow.run();
		return (T) flow;
	}
}
