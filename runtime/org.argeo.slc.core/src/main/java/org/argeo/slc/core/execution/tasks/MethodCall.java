/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
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
