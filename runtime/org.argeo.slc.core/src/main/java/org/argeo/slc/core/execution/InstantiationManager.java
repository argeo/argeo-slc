/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
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

import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.execution.ExecutionFlow;
import org.argeo.slc.execution.ExecutionSpecAttribute;

/** Manage parameters that need to be set during the instantiation of a flow */
public class InstantiationManager {

	private final static Log log = LogFactory
			.getLog(InstantiationManager.class);

	private ThreadLocal<Stack<ExecutionFlow>> flowStack = new ThreadLocal<Stack<ExecutionFlow>>();

	public Object createRef(String name) {

		if ((flowStack.get() == null) || flowStack.get().empty()) {
			throw new SlcException("No flow is currently initializing."
					+ " Declare ParameterRef as inner beans or prototypes.");
		}

		return getInitializingFlowParameter(name);
	}

	public void flowInitializationStarted(ExecutionFlow flow, String flowName) {
		// set the flow name if it is DefaultExecutionFlow
		if (flow instanceof DefaultExecutionFlow) {
			((DefaultExecutionFlow) flow).setBeanName(flowName);
		}

		if (log.isTraceEnabled())
			log.trace("Start initialization of " + flow.hashCode() + " ("
					+ flow + " - " + flow.getClass() + ")");

		// log.info("# flowInitializationStarted " + flowName);
		// create a stack for this thread if there is none
		if (flowStack.get() == null) {
			flowStack.set(new Stack<ExecutionFlow>());
		}
		flowStack.get().push(flow);
	}

	public void flowInitializationFinished(ExecutionFlow flow, String flowName) {
		if (log.isTraceEnabled())
			log.trace("Finish initialization of " + flow.hashCode() + " ("
					+ flow + " - " + flow.getClass() + ")");

		if (flowStack.get() != null) {
			ExecutionFlow registeredFlow = flowStack.get().pop();
			if (registeredFlow != null) {
				if (!flow.getName().equals(registeredFlow.getName()))
					throw new SlcException("Current flow is " + flow);
				// log.info("# flowInitializationFinished " + flowName);
				// initializingFlow.set(null);
			}
		} else {
			// happens for flows imported as services
			log.warn("flowInitializationFinished - Flow Stack is null");
		}
	}

	protected ExecutionFlow findInitializingFlowWithParameter(String key) {
		if ((flowStack.get() == null) || flowStack.get().empty())
			throw new SlcException("No initializing flow available.");

		// first look in the outer flow (that may override parameters)
		for (int i = 0; i < flowStack.get().size(); i++) {
			if (flowStack.get().elementAt(i).isSetAsParameter(key)) {
				return flowStack.get().elementAt(i);
			}
		}
		throw new SlcException("Key " + key + " is not set as parameter in "
				+ flowStack.get().firstElement().toString() + " (stack size="
				+ flowStack.get().size() + ")");

	}

	public Object getInitializingFlowParameter(String key) {
		return findInitializingFlowWithParameter(key).getParameter(key);
	}

	public Class<?> getInitializingFlowParameterClass(String key) {
		ExecutionSpecAttribute attr = findInitializingFlowWithParameter(key)
				.getExecutionSpec().getAttributes().get(key);
		if (attr instanceof RefSpecAttribute)
			return ((RefSpecAttribute) attr).getTargetClass();
		else if (attr instanceof PrimitiveSpecAttribute) {
			String type = ((PrimitiveSpecAttribute) attr).getType();
			Class<?> clss = PrimitiveUtils.typeAsClass(type);
			if (clss == null)
				throw new SlcException("Cannot convert type " + type
						+ " to class.");
			return clss;
		} else
			return null;
	}

	public Boolean isInFlowInitialization() {
		return (flowStack.get() != null) && !flowStack.get().empty();
	}
}
