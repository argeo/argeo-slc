package org.argeo.slc.execution;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.TargetSource;

public class ExecutionTargetSource implements TargetSource {
	private final static Log log = LogFactory
			.getLog(ExecutionTargetSource.class);

	private final String name;
	private final Class<?> targetClass;
	private final ExecutionFlow executionFlow;

	public ExecutionTargetSource(ExecutionFlow executionFlow,
			Class<?> targetClass, String name) {
		this.executionFlow = executionFlow;
		this.targetClass = targetClass;
		this.name = name;
	}

	public Object getTarget() throws Exception {
		if (log.isTraceEnabled())
			log.trace("Getting object " + name);
		Object obj = executionFlow.getParameter(name);
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

}
