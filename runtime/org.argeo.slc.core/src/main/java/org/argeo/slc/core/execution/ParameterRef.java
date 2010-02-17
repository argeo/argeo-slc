package org.argeo.slc.core.execution;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.FactoryBean;

public class ParameterRef implements FactoryBean {
	private final static Log log = LogFactory.getLog(ParameterRef.class);

	private InstantiationManager instantiationManager;
	private String name;

	/** Cached object. */
	private Object object;

	public ParameterRef() {
	}

	public ParameterRef(String name) {
		this.name = name;
	}

	public Object getObject() throws Exception {
		if (log.isTraceEnabled())
			log.debug("Parameter ref called for " + name);

		if (object == null)
			object = instantiationManager.getInitializingFlowParameter(name);
		return object;
	}

	public Class<?> getObjectType() {
		if (object == null)
			return instantiationManager.getInitializingFlowParameterClass(name);
		else
			return object.getClass();
	}

	public boolean isSingleton() {
		return true;
	}

	public void setInstantiationManager(
			InstantiationManager instantiationManager) {
		this.instantiationManager = instantiationManager;
	}

	public void setName(String name) {
		this.name = name;
	}

}
