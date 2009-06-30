package org.argeo.slc.core.execution;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.argeo.slc.execution.ExecutionSpec;
import org.argeo.slc.execution.ExecutionSpecAttribute;
import org.springframework.beans.factory.BeanNameAware;

public class DefaultExecutionSpec implements ExecutionSpec, BeanNameAware {
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
