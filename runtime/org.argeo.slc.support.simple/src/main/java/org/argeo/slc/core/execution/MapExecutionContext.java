package org.argeo.slc.core.execution;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.execution.ExecutionContext;
import org.argeo.slc.execution.ExecutionFlow;
import org.argeo.slc.execution.ExecutionSpecAttribute;

public class MapExecutionContext implements ExecutionContext {
	private final static Log log = LogFactory.getLog(MapExecutionContext.class);

	private final Stack<ExecutionFlowRuntime> stack = new Stack<ExecutionFlowRuntime>();

	// TODO: make it thread safe?
	private final Map<String, Object> variables = new HashMap<String, Object>();

	private final String uuid;

	private final Date creationDate = new Date();

	public MapExecutionContext() {
		uuid = UUID.randomUUID().toString();
	}

	public void addVariables(
			Map<? extends String, ? extends Object> variablesToAdd) {
		variables.putAll(variablesToAdd);
	}

	public void enterFlow(ExecutionFlow executionFlow) {
		ExecutionFlowRuntime runtime = new ExecutionFlowRuntime(executionFlow);
		stack.push(runtime);

		if (log.isDebugEnabled())
			log.debug(depthSpaces(stack.size()) + "=> " + executionFlow + " #"
					+ uuid + ", depth=" + stack.size());

		Map<String, ExecutionSpecAttribute> specAttrs = executionFlow
				.getExecutionSpec().getAttributes();
		for (String key : specAttrs.keySet()) {
			// ExecutionSpecAttribute esa = specAttrs.get(key);
			if (executionFlow.isSetAsParameter(key)) {
				runtime.getLocalVariables().put(key,
						executionFlow.getParameter(key));
				if (log.isTraceEnabled())
					log.trace(depthSpaces(stack.size()) + "Add '" + key
							+ "' as local variable.");
			}
		}

	}

	public Object getVariable(String key) {
		Object obj = findVariable(key);
		if (obj == null)
			throw new SlcException("Variable '" + key + "' not found.");
		return obj;
	}

	public Object findVariable(String key) {
		Object obj = null;

		// Look if the variable is set in the global execution variables
		// (i.e. the variable was overridden)
		if (variables.containsKey(key))
			obj = variables.get(key);

		// if the variable was not found, look in the stack starting at the
		// upper flows
		if (obj == null) {
			for (int i = 0; i < stack.size(); i++) {
				if (stack.get(i).getLocalVariables().containsKey(key)) {
					obj = stack.get(i).getLocalVariables().get(key);
					break;
				}
			}
		}

		return obj;
	}

	private static String depthSpaces(int depth) {
		StringBuffer buf = new StringBuffer(depth * 2);
		for (int i = 0; i < depth; i++)
			buf.append("  ");
		return buf.toString();
	}

	public void leaveFlow(ExecutionFlow executionFlow) {
		if (log.isDebugEnabled())
			log.debug(depthSpaces(stack.size()) + "<= " + executionFlow + " #"
					+ uuid + ", depth=" + stack.size());

		ExecutionFlowRuntime leftEf = stack.pop();
		if (!leftEf.getExecutionFlow().getName()
				.equals(executionFlow.getName()))
			throw new SlcException("Asked to leave " + executionFlow
					+ " but last is " + leftEf);

		leftEf.getScopedObjects().clear();
		leftEf.getLocalVariables().clear();

	}

	public void addScopedObject(String name, Object obj) {
		// TODO: check that the object is not set yet ?
		stack.peek().getScopedObjects().put(name, obj);
	}

	/** return null if not found */
	public Object findScopedObject(String name) {
		Object obj = null;
		for (int i = stack.size() - 1; i >= 0; i--) {
			if (stack.get(i).getScopedObjects().containsKey(name)) {
				obj = stack.get(i).getScopedObjects().get(name);
				break;
			}
		}
		return obj;
	}

	public String getUuid() {
		return uuid;
	}

	public Date getCreationDate() {
		return creationDate;
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
