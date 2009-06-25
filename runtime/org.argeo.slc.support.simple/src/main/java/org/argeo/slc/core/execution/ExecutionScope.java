package org.argeo.slc.core.execution;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.execution.ExecutionContext;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

public class ExecutionScope implements Scope {
	private final static Log log = LogFactory.getLog(ExecutionScope.class);

	private final ThreadLocal<ExecutionContext> executionContext = new ThreadLocal<ExecutionContext>();

	public final ThreadLocal<String> executionContextBeanName = new ThreadLocal<String>();

	public Object get(String name, ObjectFactory objectFactory) {

		if (log.isTraceEnabled())
			log.trace("Getting scoped bean " + name);

		// check if an execution context is defined for this thread
		if (executionContext.get() == null) {
			// if not, we expect objectFactory to produce an ExecutionContext
			Object obj = objectFactory.getObject();
			if (obj instanceof ExecutionContext) {
				// Check whether we are in an execution
				// FIXME: do it more properly (not static)
				// see https://www.argeo.org/bugzilla/show_bug.cgi?id=82
				if (!ExecutionAspect.inModuleExecution.get()) {
					log
							.error("An execution context is being instantiated outside a module execution."
									+ " Please check your references to execution contexts."
									+ " This may lead to unexpected behaviour and will be rejected in the future.");
					//Thread.dumpStack();
				}

				// store the ExecutionContext in the ThreadLocal
				executionContext.set((ExecutionContext) obj);
				executionContextBeanName.set(name);
				if (log.isDebugEnabled()) {
					log.debug("Execution context #"
							+ executionContext.get().getUuid()
							+ " instantiated. (beanName="
							+ executionContextBeanName.get() + ")");
					// Thread.dumpStack();
				}
				return obj;
			} else {
				throw new SlcException(
						"Expected an ExecutionContext, got an object of class "
								+ obj.getClass()
								+ " for bean "
								+ name
								+ ": make sure that you have porperly set scope=\"execution\" where required");
			}
		}

		if (name.equals(executionContextBeanName.get())) {
			return executionContext.get();
		} else {
			// see if the executionContext already knows the object
			Object obj = executionContext.get().findScopedObject(name);
			if (obj == null) {
				obj = objectFactory.getObject();
				if (!(obj instanceof ExecutionContext)) {
					executionContext.get().addScopedObject(name, obj);
				} else {
					throw new SlcException(
							"Only one ExecutionContext can be defined per Thread");
				}
			}
			return obj;
		}

		// if (ExecutionContext.getScopedObjects().containsKey(name)) {
		// // returns cached instance
		// Object obj = ExecutionContext.getScopedObjects().get(name);
		// if (log.isTraceEnabled())
		// log.trace("Return cached scoped object " + obj);
		// return obj;
		// } else {
		// // creates instance
		// Object obj = objectFactory.getObject();
		// ExecutionContext.getScopedObjects().put(name, obj);
		// if (log.isTraceEnabled())
		// log.trace("Created regular scoped object " + obj);
		// return obj;
		// }
	}

	public String getConversationId() {

		return executionContext.get().getUuid();
	}

	public Boolean hasExecutionContext() {
		return executionContext.get() != null;
	}

	public void registerDestructionCallback(String name, Runnable callback) {
		// TODO: implement it
		// throw new UnsupportedOperationException();
	}

	public Object remove(String name) {
		log.debug("Remove object " + name);
		throw new UnsupportedOperationException();
	}

}
