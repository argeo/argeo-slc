package org.argeo.slc.core.execution.tasks;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

public class MethodCall implements Runnable {
	private Object target;
	private String method;
	private List<Object> args = new ArrayList<Object>();

	public void run() {
		Assert.notNull(target, "target");
		Assert.notNull(method, "method");
		Method methodRef = ReflectionUtils
				.findMethod(target.getClass(), method);
		if (args.size() == 0)
			ReflectionUtils.invokeMethod(methodRef, target);
		else
			ReflectionUtils.invokeMethod(methodRef, methodRef, args.toArray());
	}

	public void setTarget(Object target) {
		this.target = target;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public void setArgs(List<Object> args) {
		this.args = args;
	}

}
