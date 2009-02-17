package org.argeo.slc.executionflow;

import java.util.HashMap;
import java.util.Map;

import org.springframework.aop.framework.ProxyFactory;

public class SimpleExecutionSpec implements ExecutionSpec {
	private Map<String, ExecutionSpecAttribute> attributes = new HashMap<String, ExecutionSpecAttribute>();

	public Map<String, ExecutionSpecAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, ExecutionSpecAttribute> attributes) {
		this.attributes = attributes;
	}

	public Object createRef(String name) {
		RefSpecAttribute<Object> refSpecAttribute = (RefSpecAttribute<Object>) attributes
				.get(name);
		Class targetClass = refSpecAttribute.getTargetClass();
		ExecutionTargetSource targetSource = new ExecutionTargetSource();
		targetSource.setName(name);
		targetSource.setTargetClass(targetClass);
		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.setTargetClass(targetClass);
		proxyFactory.setProxyTargetClass(true);
		proxyFactory.setTargetSource(targetSource);
		
		return proxyFactory.getProxy();
	}
}
