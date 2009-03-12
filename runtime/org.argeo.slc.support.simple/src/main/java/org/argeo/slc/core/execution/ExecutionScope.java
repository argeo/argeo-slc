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

	private final ThreadLocal<ExecutionContext> executionContext 
			= new ThreadLocal<ExecutionContext>();
	
	public final ThreadLocal<String> executionContextBeanName = new ThreadLocal<String>();
	
	public Object get(String name, ObjectFactory objectFactory) {

		if (log.isTraceEnabled())
			log.trace("Getting scoped bean " + name);
		
		// check if an execution context is defined for this thread
		if(executionContext.get() == null) {
			// if not, we expect objectFactory to produce an ExecutionContext
			Object obj = objectFactory.getObject();
			if(obj instanceof ExecutionContext) {
				// store the ExecutionContext in the ThreadLocal
				executionContext.set((ExecutionContext)obj);
				executionContextBeanName.set(name);
				return obj;
			}
			else {
				throw new SlcException("Expected an ExecutionContext, got an object of class "
						+ obj.getClass() + " for bean " + name);
			}			
		}
		
		if(name.equals(executionContextBeanName.get())) {
			return executionContext.get();
		}
		else {
			// see if the executionContext already knows the object 
			Object obj = executionContext.get().findScopedObject(name);
			if(obj == null) {
				obj = objectFactory.getObject();
				if(!(obj instanceof ExecutionContext)) {
					executionContext.get().addScopedObject(name, obj);
				}
				else {
					throw new SlcException("Only one ExecutionContext can be defined per Thread");
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
		//throw new UnsupportedOperationException();
	}

	public Object remove(String name) {
		log.debug("Remove object " + name);
		throw new UnsupportedOperationException();
	}

}
