/*
 * Copyright (C) 2007-2012 Mathieu Baudier
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
/** Aspect intercepting calls on execution flows and contexts. */
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

		logStackEvent("=> ", executionFlow);
		try {
			// Actually execute the flow
			pjp.proceed();
		} finally {
			logStackEvent("<= ", executionFlow);
			executionStack.leaveFlow(executionFlow);
		}
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
		if (log.isTraceEnabled())
			log.debug(depthSpaces(stackSize) + symbol + executionFlow + " #"
					+ executionStack.getCurrentStackLevelUuid() + ", depth="
					+ stackSize);
		if (log.isDebugEnabled())
			log.debug(depthSpaces(stackSize) + symbol + executionFlow);
	}

	protected void logRunnableExecution(ExecutionFlow executionFlow,
			Runnable runnable) {
		Integer stackSize = executionStack.getStackSize();
		if (log.isDebugEnabled())
			log.debug(depthSpaces(stackSize + 1)
					+ runnable.getClass().getSimpleName() + " in "
					+ executionFlow);
	}

	private String depthSpaces(int depth) {
		StringBuffer buf = new StringBuffer(depth * 2);
		for (int i = 0; i < depth; i++)
			buf.append("  ");
		return buf.toString();
	}

}
