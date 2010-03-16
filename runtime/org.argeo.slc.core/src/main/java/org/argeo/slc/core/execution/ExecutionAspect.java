package org.argeo.slc.core.execution;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.execution.ExecutionContext;
import org.argeo.slc.execution.ExecutionFlow;
import org.argeo.slc.execution.ExecutionStack;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class ExecutionAspect {
	private final static Log log = LogFactory.getLog(ExecutionAspect.class);

	private ExecutionStack executionStack;
	private ExecutionContext executionContext;

	@Around("flowExecution()")
	public void aroundFlow(ProceedingJoinPoint pjp) throws Throwable {
		// IMPORTANT: Make sure that the execution context is called before the
		// execution stack
		executionContext.getUuid();

		ExecutionFlow executionFlow = (ExecutionFlow) pjp.getTarget();
		executionStack.enterFlow(executionFlow);
		executionContext.setVariable(ExecutionContext.VAR_FLOW_ID,
				executionStack.getCurrentStackLevelUuid());
		executionContext.setVariable(ExecutionContext.VAR_FLOW_NAME,
				executionFlow.getName());

		if (log.isDebugEnabled())
			logStackEvent("=> ", executionFlow);
		
		try {
			// Actually execute the flow
			pjp.proceed();
		} finally {
			if (log.isDebugEnabled())
				logStackEvent("<= ", executionFlow);
	
			executionStack.leaveFlow(executionFlow);
		}
	}

	@Around("runnableExecution()")
	public void aroundRunnable(ProceedingJoinPoint pjp) throws Throwable {
		ExecutionFlow executionFlow = (ExecutionFlow) pjp.getTarget();
		Runnable runnable = (Runnable) pjp.getArgs()[0];
		if (log.isDebugEnabled())
			logRunnableExecution(executionFlow, runnable);
		// Actually execute the runnable
		pjp.proceed();
	}

	@Around("getVariable()")
	public Object aroundGetVariable(ProceedingJoinPoint pjp) throws Throwable {
		Object obj = pjp.proceed();
		// if the variable was not found, look in the stack starting at the
		// upper flows
		if (obj == null) {
			String key = pjp.getArgs()[0].toString();
			obj = executionStack.findLocalVariable(key);
		}
		return obj;
	}

	@Pointcut("execution(void org.argeo.slc.execution.ExecutionFlow.run())")
	public void flowExecution() {
	}

	@Pointcut("execution(void org.argeo.slc.execution.ExecutionFlow.doExecuteRunnable(..))")
	public void runnableExecution() {
	}

	@Pointcut("execution(* org.argeo.slc.execution.ExecutionContext.getVariable(..))")
	public void getVariable() {
	}

	public void setExecutionStack(ExecutionStack executionStack) {
		this.executionStack = executionStack;
	}

	public void setExecutionContext(ExecutionContext executionContext) {
		this.executionContext = executionContext;
	}

	protected void logStackEvent(String symbol, ExecutionFlow executionFlow) {
		Integer stackSize = executionStack.getStackSize();
		log.debug(depthSpaces(stackSize) + symbol + executionFlow + " #"
				+ executionStack.getCurrentStackLevelUuid() + ", depth="
				+ stackSize);
	}

	protected void logRunnableExecution(ExecutionFlow executionFlow,
			Runnable runnable) {
		Integer stackSize = executionStack.getStackSize();
		log.debug(depthSpaces(stackSize + 1)
				+ runnable.getClass().getSimpleName() + " in " + executionFlow);
	}

	private String depthSpaces(int depth) {
		StringBuffer buf = new StringBuffer(depth * 2);
		for (int i = 0; i < depth; i++)
			buf.append("  ");
		return buf.toString();
	}

}
