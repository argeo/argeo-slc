package org.argeo.slc.core.execution;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.execution.ExecutionFlow;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class ExecutionAspect {
	private static Log log = LogFactory.getLog(ExecutionAspect.class);

	@Before("flowExecution()")
	public void beforeFlow(JoinPoint jp) throws Throwable {
		//log.debug("this " + jp.getThis().getClass());
		//log.debug("target " + jp.getTarget().getClass());
		// Thread.dumpStack();
		ExecutionFlow executionFlow = (ExecutionFlow) jp.getTarget();
		ExecutionContext.enterFlow(executionFlow);
	}

	@After("flowExecution()")
	public void afterFlow(JoinPoint jp) throws Throwable {
		//log.debug("this " + jp.getThis().getClass());
		//log.debug("target " + jp.getTarget().getClass());
		ExecutionFlow executionFlow = (ExecutionFlow) jp.getTarget();
		ExecutionContext.leaveFlow(executionFlow);
	}

	@Pointcut("execution(void org.argeo.slc.execution.ExecutionFlow.execute())")
	public void flowExecution() {
	}
}
