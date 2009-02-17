package org.argeo.slc.executionflow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.TargetSource;

public class ExecutionTargetSource implements TargetSource {
	private final static Log log = LogFactory
			.getLog(ExecutionTargetSource.class);

	private String name;
	private Class targetClass;

	public Object getTarget() throws Exception {
		if (log.isTraceEnabled())
			log.trace("Getting object " + name);
		ExecutionFlow executionFlow = SimpleExecutionFlow
				.getCurrentExecutionFlow();
		Object obj = executionFlow.getAttributes().get(name);
		if (log.isTraceEnabled())
			log.trace("Target object " + obj);
		return obj;
	}

	public Class getTargetClass() {
		return targetClass;
	}

	public boolean isStatic() {
		return false;
	}

	public void releaseTarget(Object target) throws Exception {
		// TODO Auto-generated method stub

	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTargetClass(Class targetClass) {
		this.targetClass = targetClass;
	}

}
