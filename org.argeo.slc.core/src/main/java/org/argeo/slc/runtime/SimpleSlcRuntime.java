package org.argeo.slc.runtime;

import org.springframework.context.ApplicationContext;

public class SimpleSlcRuntime implements SlcRuntime {
	private ApplicationContext runtimeContext;

	public SimpleSlcRuntime(ApplicationContext runtimeContext) {
		this.runtimeContext = runtimeContext;
	}

	public ApplicationContext getRuntimeContext() {
		return runtimeContext;
	}

}
