package org.argeo.slc.core.execution;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.execution.ExecutionFlow;
import org.argeo.slc.execution.ExecutionSpecAttribute;
import org.argeo.slc.execution.ExecutionStack;

public class DefaultExecutionStack implements ExecutionStack {

	private final static Log log = LogFactory
			.getLog(DefaultExecutionStack.class);

	private final Stack<ExecutionFlowRuntime> stack = new Stack<ExecutionFlowRuntime>();

	public synchronized void enterFlow(ExecutionFlow executionFlow) {
		ExecutionFlowRuntime runtime = new ExecutionFlowRuntime(executionFlow);
		stack.push(runtime);

		Map<String, ExecutionSpecAttribute> specAttrs = executionFlow
				.getExecutionSpec().getAttributes();
		for (String key : specAttrs.keySet()) {
			if (executionFlow.isSetAsParameter(key)) {
				runtime.getLocalVariables().put(key,
						executionFlow.getParameter(key));
			}
		}
	}

	public synchronized String getCurrentStackLevelUuid() {
		return stack.peek().getUuid();
	}

	public synchronized Integer getStackSize() {
		return stack.size();
	}

	/**
	 * Looks for a set variable in the stack, starting at the upper flows
	 * 
	 * @return the variable or <code>null</code> if not found
	 */
	public synchronized Object findLocalVariable(String key) {
		Object obj = null;
		for (int i = 0; i < stack.size(); i++) {
			if (stack.get(i).getLocalVariables().containsKey(key)) {
				obj = stack.get(i).getLocalVariables().get(key);
				break;
			}
		}
		return obj;
	}

	public synchronized void leaveFlow(ExecutionFlow executionFlow) {
		ExecutionFlowRuntime leftEf = stack.pop();

		if (!leftEf.getExecutionFlow().getName()
				.equals(executionFlow.getName()))
			throw new SlcException("Asked to leave " + executionFlow
					+ " but last is " + leftEf);

		leftEf.getScopedObjects().clear();
		leftEf.getLocalVariables().clear();
	}

	public synchronized void addScopedObject(String name, Object obj) {
		ExecutionFlowRuntime runtime = stack.peek();
		// TODO: check that the object is not set yet ?
		if (log.isDebugEnabled()) {
			Object existing = findScopedObject(name);
			if (existing != null)
				log.warn("Scoped object " + name + " of type " + obj.getClass()
						+ " already registered in " + runtime);
		}
		runtime.getScopedObjects().put(name, obj);
	}

	/** @return </code>null<code> if not found */
	public synchronized Object findScopedObject(String name) {
		Object obj = null;
		for (int i = stack.size() - 1; i >= 0; i--) {
			if (stack.get(i).getScopedObjects().containsKey(name)) {
				obj = stack.get(i).getScopedObjects().get(name);
				break;
			}
		}
		return obj;
	}

	protected static class ExecutionFlowRuntime {
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

		@Override
		public String toString() {
			return "Stack Level #" + uuid;
		}

	}
}
