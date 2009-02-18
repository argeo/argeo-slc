package org.argeo.slc.executionflow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

public class ExecutionScope implements Scope {
	private final static Log log = LogFactory.getLog(ExecutionScope.class);

	public Object get(String name, ObjectFactory objectFactory) {

		if (log.isTraceEnabled())
			log.info("Getting scoped bean " + name);
		ExecutionFlow executionFlow = ExecutionContext.getCurrentFlow();
		// returns cached instance
		if (executionFlow.getScopedObjects().containsKey(name)) {
			Object obj = executionFlow.getScopedObjects().get(name);
			if (log.isTraceEnabled())
				log.info("Return cached scoped object " + obj);
			return obj;
		}
		// creates instance
		Object obj = objectFactory.getObject();
		if (obj instanceof ExecutionFlow) {
			// add to itself (it is not yet the current flow)
			((ExecutionFlow) obj).getScopedObjects().put(name, obj);
			if (log.isTraceEnabled())
				log.info("Cached flow object " + obj + " in itself");
		} else {
			executionFlow.getScopedObjects().put(name, obj);
			if (log.isTraceEnabled())
				log.info("Created regular scoped object " + obj);
		}
		return obj;
	}

	public String getConversationId() {
		ExecutionFlow executionFlow = ExecutionContext.getCurrentFlow();
		return executionFlow.getUuid();
	}

	public void registerDestructionCallback(String name, Runnable callback) {
		throw new UnsupportedOperationException();
	}

	public Object remove(String name) {
		log.debug("Remove object " + name);
		throw new UnsupportedOperationException();
	}

}
