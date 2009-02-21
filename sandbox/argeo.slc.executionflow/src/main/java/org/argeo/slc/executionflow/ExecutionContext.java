package org.argeo.slc.executionflow;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.process.SlcExecution;
import org.springframework.beans.factory.ObjectFactory;

public class ExecutionContext {
	private final static Log log = LogFactory.getLog(ExecutionContext.class);

	private final static ThreadLocal<ExecutionContext> executionContext = new ThreadLocal<ExecutionContext>();

	private final Stack<ExecutionFlowRuntime> stack = new Stack<ExecutionFlowRuntime>();

	// TODO: make it thread safe?
	private final Map<String, Object> variables = new HashMap<String, Object>();

	private final String uuid = UUID.randomUUID().toString();

	public static Map<String, Object> getVariables() {
		if (executionContext.get() == null)
			return null;
		return executionContext.get().variables;
	}

	public static ExecutionContext getCurrent() {
		return executionContext.get();
	}

	public static String getExecutionUuid() {
		if (executionContext.get() == null)
			return null;
		return executionContext.get().getUuid();
	}

	public static void registerExecutionContext(ExecutionContext context) {
		if (executionContext.get() != null)
			throw new SlcException("Context #" + executionContext.get().uuid
					+ " already registered.");
		executionContext.set(context);
	}

	public static void enterFlow(ExecutionFlow executionFlow) {
		Stack<ExecutionFlowRuntime> stack = executionContext.get().stack;

		ExecutionFlowRuntime runtime = new ExecutionFlowRuntime(executionFlow);
		stack.push(runtime);

		if (log.isTraceEnabled())
			log.debug(depthSpaces(stack.size()) + "=> " + executionFlow + " #"
					+ getCurrentStackUuid() + ", depth=" + stack.size());

		Map<String, ExecutionSpecAttribute> specAttrs = executionFlow
				.getExecutionSpec().getAttributes();
		for (String key : specAttrs.keySet()) {
			ExecutionSpecAttribute esa = specAttrs.get(key);
			if (esa.getIsParameter()) {
				runtime.getLocalVariables().put(key,
						executionFlow.getParameter(key));
				if (log.isTraceEnabled())
					log.trace(depthSpaces(stack.size()) + "Add '" + key
							+ "' as local variable.");
			}
		}

	}

	public static Object getVariable(String key) {
		Object obj = getWithCheck().findVariable(key);
		if (obj == null)
			throw new SlcException("Variable '" + key + "' not found.");
		return obj;
	}

	protected Object findVariable(String key) {
		Object obj = null;
		for (int i = stack.size() - 1; i >= 0; i--) {
			if (stack.get(i).getLocalVariables().containsKey(key)) {
				obj = stack.get(i).getLocalVariables().get(key);
				break;
			}
		}

		// Look into global execution variables
		if (obj == null) {
			if (variables.containsKey(key))
				obj = variables.get(key);
		}

		return obj;
	}

	private static String depthSpaces(int depth) {
		StringBuffer buf = new StringBuffer(depth * 2);
		for (int i = 0; i < depth; i++)
			buf.append("  ");
		return buf.toString();
	}

	public static void leaveFlow(ExecutionFlow executionFlow) {
		Stack<ExecutionFlowRuntime> stack = executionContext.get().stack;
		if (log.isTraceEnabled())
			log.debug(depthSpaces(stack.size()) + "<= " + executionFlow + " #"
					+ getCurrentStackUuid() + ", depth=" + stack.size());

		ExecutionFlowRuntime leftEf = stack.pop();
		if (!leftEf.getExecutionFlow().getUuid()
				.equals(executionFlow.getUuid()))
			throw new SlcException("Asked to leave " + executionFlow
					+ " but last is " + leftEf);

		leftEf.getScopedObjects().clear();
		leftEf.getLocalVariables().clear();

	}

	public static String getCurrentStackUuid() {
		return getWithCheck().stack.peek().uuid;
	}

	// public static ExecutionFlow getCurrentFlow() {
	// return getWithCheck().stack.peek().executionFlow;
	// }

	public static Boolean isExecuting() {
		return executionContext.get() != null;
	}

	protected static ExecutionContext getWithCheck() {
		if (executionContext.get() == null)
			throw new SlcException("No execution context");
		return executionContext.get();
	}

	public static Object findOrAddScopedObject(String name,
			ObjectFactory objectFactory) {
		ExecutionContext executionContext = getWithCheck();
		Object obj = executionContext.findScopedObject(name);
		if (obj == null) {
			obj = objectFactory.getObject();
			executionContext.stack.peek().getScopedObjects().put(name, obj);
		}
		return obj;
	}

	/** return null if not found */
	protected Object findScopedObject(String key) {
		Object obj = null;
		for (int i = stack.size() - 1; i >= 0; i--) {
			if (stack.get(i).getScopedObjects().containsKey(key)) {
				obj = stack.get(i).getScopedObjects().get(key);
				break;
			}
		}
		return obj;
	}

	public String getUuid() {
		return uuid;
	}

	private static class ExecutionFlowRuntime {
		private final ExecutionFlow executionFlow;
		private final Map<String, Object> scopedObjects = new HashMap<String, Object>();
		private final Map<String, Object> localVariables = new HashMap<String, Object>();
		private final String uuid = UUID.randomUUID().toString();

		public ExecutionFlowRuntime(ExecutionFlow executionFlow) {
			this.executionFlow = executionFlow;
		}

		public ExecutionFlow getExecutionFlow() {
			return executionFlow;
		}

		public Map<String, Object> getScopedObjects() {
			return scopedObjects;
		}

		public String getUuid() {
			return uuid;
		}

		public Map<String, Object> getLocalVariables() {
			return localVariables;
		}

	}
}
