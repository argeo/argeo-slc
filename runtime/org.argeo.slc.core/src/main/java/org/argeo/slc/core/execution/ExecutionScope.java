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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.UnsupportedException;
import org.argeo.slc.execution.ExecutionContext;
import org.argeo.slc.execution.ExecutionFlow;
import org.argeo.slc.execution.ExecutionSpec;
import org.argeo.slc.execution.ExecutionStack;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

public class ExecutionScope implements Scope {
	private final static Log log = LogFactory.getLog(ExecutionScope.class);

	private final ThreadLocal<ExecutionStack> executionStack = new ThreadLocal<ExecutionStack>();
	public final ThreadLocal<String> executionStackBeanName = new ThreadLocal<String>();

	private final ThreadLocal<ExecutionContext> executionContext = new ThreadLocal<ExecutionContext>();
	private final ThreadLocal<String> executionContextBeanName = new ThreadLocal<String>();

	public Object get(String name, ObjectFactory objectFactory) {
		if (log.isTraceEnabled())
			log.debug("Get execution scoped bean " + name);

		// shortcuts
		if (executionStackBeanName.get() != null
				&& name.equals(executionStackBeanName.get())) {
			return executionStack.get();
		}

		if (executionContextBeanName.get() != null
				&& name.equals(executionContextBeanName.get())) {
			return executionContext.get();
		}

		// execution context must be defined first
		if (executionContext.get() == null) {
			Object obj = objectFactory.getObject();
			if (obj instanceof ExecutionContext) {
				return dealWithSpecialScopedObject(name, executionContext,
						executionContextBeanName, (ExecutionContext) obj);
			} else {
				// TODO: use execution context wrapper
				throw new SlcException("No execution context has been defined.");
			}
		}

		// for other scoped objects, an executions stack must be available
		if (executionStack.get() == null) {
			Object obj = objectFactory.getObject();
			if (obj instanceof ExecutionStack) {
				return dealWithSpecialScopedObject(name, executionStack,
						executionStackBeanName, (ExecutionStack) obj);
			} else {
				throw new SlcException("No execution stack has been defined.");
			}
		}

		// see if the execution stack already knows the object
		Object obj = executionStack.get().findScopedObject(name);
		if (obj == null) {
			obj = objectFactory.getObject();
			if (obj instanceof ExecutionContext)
				throw new SlcException(
						"Only one execution context can be defined per thread");
			if (obj instanceof ExecutionStack)
				throw new SlcException(
						"Only one execution stack can be defined per thread");

			checkForbiddenClasses(obj);

			executionStack.get().addScopedObject(name, obj);
		}
		return obj;

	}

	protected <T> T dealWithSpecialScopedObject(String name,
			ThreadLocal<T> threadLocal,
			ThreadLocal<String> threadLocalBeanName, T newObj) {

		T obj = threadLocal.get();
		if (obj == null) {
			obj = newObj;
			threadLocal.set(obj);
			threadLocalBeanName.set(name);
			if (log.isTraceEnabled()) {
				log.debug(obj.getClass() + " instantiated. (beanName=" + name
						+ ")");
			}
			return obj;
		} else {
			throw new SlcException("Only one scoped " + obj.getClass()
					+ " can be defined per thread");
		}

	}

	protected void checkForbiddenClasses(Object obj) {
		Class<?> clss = obj.getClass();
		if (ExecutionFlow.class.isAssignableFrom(clss)
				|| ExecutionSpec.class.isAssignableFrom(clss)) {
			throw new UnsupportedException("Execution scoped object", clss);
		}
	}

	public String getConversationId() {
		// TODO: is it the most relevant?
		return executionContext.get().getUuid();
	}

	public void registerDestructionCallback(String name, Runnable callback) {
		// TODO: implement it
		// throw new UnsupportedOperationException();
	}

	public Object remove(String name) {
		if (log.isDebugEnabled())
			log.debug("Remove object " + name);
		throw new UnsupportedOperationException();
	}

	public Object resolveContextualObject(String key) {
		return executionContext.get().getVariable(key);
	}

}
