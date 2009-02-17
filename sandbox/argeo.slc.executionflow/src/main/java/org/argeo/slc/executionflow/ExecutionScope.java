package org.argeo.slc.executionflow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

public class ExecutionScope implements Scope {
	private final static Log log = LogFactory.getLog(ExecutionScope.class);
	
	public Object get(String name, ObjectFactory objectFactory) {
		log.info("Getting bean "+name);
		ExecutionFlow executionFlow = SimpleExecutionFlow.getCurrentExecutionFlow();
		Object obj = executionFlow.getAttributes().get(name);
		log.info("Scoped object "+obj);
		return obj;
	}

	public String getConversationId() {
		ExecutionFlow executionFlow = SimpleExecutionFlow.getCurrentExecutionFlow();
		return executionFlow.getUuid();
	}

	public void registerDestructionCallback(String name, Runnable callback) {
		// TODO Auto-generated method stub

	}

	public Object remove(String name) {
		// TODO Auto-generated method stub
		return null;
	}

}
