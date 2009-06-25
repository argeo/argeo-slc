package org.argeo.slc.core.execution;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.execution.ExecutionFlow;
import org.argeo.slc.execution.ExecutionSpec;
import org.argeo.slc.execution.ExecutionSpecAttribute;
import org.springframework.beans.factory.BeanNameAware;

public class DefaultExecutionSpec implements ExecutionSpec, BeanNameAware {
	private final static Log log = LogFactory
			.getLog(DefaultExecutionSpec.class);
	
//	private final static ThreadLocal<Stack<ExecutionFlow> > flowStack = new ThreadLocal<Stack<ExecutionFlow> >();

	private Map<String, ExecutionSpecAttribute> attributes = new HashMap<String, ExecutionSpecAttribute>();

	private String name = getClass().getName() + "#" + UUID.randomUUID();

	public Map<String, ExecutionSpecAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, ExecutionSpecAttribute> attributes) {
		this.attributes = attributes;
	}

	public void setBeanName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public boolean equals(Object obj) {
		return ((ExecutionSpec) obj).getName().equals(name);
	}

}
