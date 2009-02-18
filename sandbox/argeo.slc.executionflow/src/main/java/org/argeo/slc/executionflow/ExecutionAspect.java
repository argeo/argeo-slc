package org.argeo.slc.executionflow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

	// @Around("execution(void org.argeo.slc.executionflow.ExecutionFlow.execute()) && target(org.argeo.slc.executionflow.ExecutionFlow)")
	public void registerFlow(ProceedingJoinPoint pjp) throws Throwable {
		try {
			log.debug("registerFlow " + pjp.getTarget().getClass());
			ExecutionContext.enterFlow((ExecutionFlow) pjp.getTarget());
			pjp.proceed();
		} finally {
			ExecutionContext.leaveFlow((ExecutionFlow) pjp.getTarget());
		}
	}

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

	@Pointcut("execution(void org.argeo.slc.executionflow.ExecutionFlow.execute())")
	public void flowExecution() {
	}
}
