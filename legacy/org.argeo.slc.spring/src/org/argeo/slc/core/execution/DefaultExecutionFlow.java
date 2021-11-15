package org.argeo.slc.core.execution;

import java.util.Map;

import org.argeo.slc.execution.ExecutionSpec;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;

/** Default implementation of an execution flow. */
@Deprecated
public class DefaultExecutionFlow extends org.argeo.slc.runtime.DefaultExecutionFlow
		implements InitializingBean, BeanNameAware {
	public DefaultExecutionFlow() {
		super();
	}

	public DefaultExecutionFlow(ExecutionSpec executionSpec, Map<String, Object> parameters) {
		super(executionSpec, parameters);
	}

	public DefaultExecutionFlow(ExecutionSpec executionSpec) {
		super(executionSpec);
	}

	public void afterPropertiesSet() throws Exception {
		init();
	}

	public void setBeanName(String name) {
		setName(name);
	}
}
