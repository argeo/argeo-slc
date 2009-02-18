package org.argeo.slc.executionflow;

import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;

public class ExecutionContext {
	private final static Log log = LogFactory.getLog(ExecutionContext.class);

	private final static ThreadLocal<ExecutionContext> executionContext = new ThreadLocal<ExecutionContext>();

	private final Stack<ExecutionFlow> stack = new Stack<ExecutionFlow>();

	public static ExecutionFlow getCurrentFlow() {
		if (executionContext.get() == null)
			return null;
		return executionContext.get().stack.peek();
	}

	public static void enterFlow(ExecutionFlow executionFlow) {
		if (executionContext.get() == null) {
			// TODO: deal with parallell flows
			executionContext.set(new ExecutionContext());
		}
		Stack<ExecutionFlow> stack = executionContext.get().stack;
		stack.push(executionFlow);
		if (log.isDebugEnabled())
			log.debug("Depth: " + stack.size() + ". Enter " + executionFlow);
	}

	public static void leaveFlow(ExecutionFlow executionFlow) {
		Stack<ExecutionFlow> stack = executionContext.get().stack;
		if (log.isDebugEnabled())
			log.debug("Depth: " + stack.size() + ". Leave " + executionFlow);
		ExecutionFlow leftEf = stack.pop();
		leftEf.getScopedObjects().clear();
		if (!leftEf.getUuid().equals(executionFlow.getUuid())) {
			throw new SlcException("Asked to leave " + executionFlow
					+ " but last is " + leftEf);
		}
	}
}
