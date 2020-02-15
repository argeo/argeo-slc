/*
 * Copyright (C) 2007-2012 Argeo GmbH
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
import org.argeo.slc.runtime.InstantiationManager;
import org.springframework.beans.factory.FactoryBean;

public class ParameterRef implements FactoryBean<Object> {
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
