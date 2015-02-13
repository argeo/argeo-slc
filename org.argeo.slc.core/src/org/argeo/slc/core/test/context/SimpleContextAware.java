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
package org.argeo.slc.core.test.context;

import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.InitializingBean;

import org.argeo.slc.SlcException;
import org.argeo.slc.test.context.ContextAware;
import org.argeo.slc.test.context.ParentContextAware;

public class SimpleContextAware implements ContextAware, InitializingBean {
	private ParentContextAware parentContext;

	private Map<String, Object> values = new TreeMap<String, Object>();
	private Map<String, Object> expectedValues = new TreeMap<String, Object>();

	private String contextSkipFlag = DEFAULT_SKIP_FLAG;
	private String contextAnyFlag = DEFAULT_ANY_FLAG;

	public Map<String, Object> getValues() {
		return values;
	}

	public void setValues(Map<String, Object> values) {
		this.values = values;
	}

	public Map<String, Object> getExpectedValues() {
		return expectedValues;
	}

	public void setExpectedValues(Map<String, Object> expectedValues) {
		this.expectedValues = expectedValues;
	}

	/** Used to add this context as a child by setting a property. */
	public void setParentContext(ParentContextAware parentContextAware) {
		if (parentContext != null)
			throw new SlcException("Parent context already set");
		this.parentContext = parentContextAware;
		this.parentContext.addChildContext(this);
	}

	protected ParentContextAware getParentContext() {
		return parentContext;
	}

	public void afterPropertiesSet() throws Exception {
		if (parentContext != null) {
			ContextUtils.synchronize(parentContext);
		}
	}

	public String getContextSkipFlag() {
		return contextSkipFlag;
	}

	public void setContextSkipFlag(String contextSkipFlag) {
		this.contextSkipFlag = contextSkipFlag;
	}

	public String getContextAnyFlag() {
		return contextAnyFlag;
	}

	public void setContextAnyFlag(String contextAnyFlag) {
		this.contextAnyFlag = contextAnyFlag;
	}

}
