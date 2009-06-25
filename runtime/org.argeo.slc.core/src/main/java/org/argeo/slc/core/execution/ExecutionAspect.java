package org.argeo.slc.core.execution;

import org.argeo.slc.execution.ExecutionContext;
import org.argeo.slc.execution.ExecutionFlow;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class ExecutionAspect {
	static ThreadLocal<Boolean> inModuleExecution = new ThreadLocal<Boolean>() {
		protected Boolean initialValue() {
			return false;
		}
	};

	private ExecutionContext executionContext;

	public ExecutionContext getExecutionContext() {
		return executionContext;
	}

	public void setExecutionContext(ExecutionContext executionContext) {
		this.executionContext = executionContext;
	}

	@Before("flowExecution()")
	public void beforeFlow(JoinPoint jp) throws Throwable {
		ExecutionFlow executionFlow = (ExecutionFlow) jp.getTarget();
		executionContext.enterFlow(executionFlow);
	}

	@After("flowExecution()")
	public void afterFlow(JoinPoint jp) throws Throwable {
		ExecutionFlow executionFlow = (ExecutionFlow) jp.getTarget();
		executionContext.leaveFlow(executionFlow);
	}

	@Before("moduleExecution()")
	public void beforeModuleExecution(JoinPoint jp) throws Throwable {
		inModuleExecution.set(true);
	}

	@After("moduleExecution()")
	public void afterModuleExecution(JoinPoint jp) throws Throwable {
		inModuleExecution.set(false);
	}

	@Pointcut("execution(void org.argeo.slc.execution.ExecutionFlow.run())")
	public void flowExecution() {
	}

	@Pointcut("execution(void org.argeo.slc.execution.ExecutionModule.execute(..))")
	public void moduleExecution() {
	}

}
