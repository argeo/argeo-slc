package org.argeo.slc.executionflow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

public class ExecutionScope implements Scope {
	private final static Log log = LogFactory.getLog(ExecutionScope.class);

	public Object get(String name, ObjectFactory objectFactory) {

		if (log.isTraceEnabled())
			log.trace("Getting scoped bean " + name);
		return ExecutionContext.findOrAddScopedObject(name, objectFactory);

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
		return ExecutionContext.getCurrentStackUuid();
	}

	public void registerDestructionCallback(String name, Runnable callback) {
		throw new UnsupportedOperationException();
	}

	public Object remove(String name) {
		log.debug("Remove object " + name);
		throw new UnsupportedOperationException();
	}

}
