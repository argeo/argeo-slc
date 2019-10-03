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
package org.argeo.slc.execution;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Implements both archetype and implementation of a given process.
 * 
 * At specification time, <code>executionSpec</code> represents the spec of the
 * parameters accepted by the process, with, among others: type, default value
 * and, optionally, possible values for each parameter. Thus ExecutionSpec might
 * be a huge object. Note that when marshalling only a reference to a specific
 * ExecutionSpec is stored in the XML to optimize performance and avoid
 * redundancy between various ExecutionFlowDesciptor that might have the same
 * ExecutionSpec.
 * 
 * At runtime, we build a RealizedFlow which references an
 * ExecutionFlowDescriptor. As it happens AFTER marshalling / unmarshalling
 * process, the ExecutionSpec is null but we manage to retrieve the
 * ExecutionSpec and store it in the RealizedFlow, whereas set values of the
 * parameters are stored in the <code>values</code> map.
 * 
 * Generally, values object are either a <code>PrimitiveAccessor</code> or a
 * <code>RefValue</code> but can be other objects.
 */
public class ExecutionFlowDescriptor implements Serializable, Cloneable {
	private static final long serialVersionUID = 7101944857038041216L;
	private String name;
	private String description;
	private String path;
	private Map<String, Object> values;
	private ExecutionSpec executionSpec;

	public ExecutionFlowDescriptor() {
	}

	public ExecutionFlowDescriptor(String name, String description,
			Map<String, Object> values, ExecutionSpec executionSpec) {
		this.name = name;
		this.values = values;
		this.executionSpec = executionSpec;
	}

	/** The referenced {@link ExecutionSpec} is NOT cloned. */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new ExecutionFlowDescriptor(name, description,
				new HashMap<String, Object>(values), executionSpec);
	}

	public String getName() {
		return name;
	}

	/**
	 * @deprecated will be removed in SLC 2.x, the path should be the part of
	 *             the name with '/'
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @deprecated will be removed in SLC 2.0, the path should be the part of
	 *             the name with '/'
	 */
	public void setPath(String path) {
		this.path = path;
	}

	public Map<String, Object> getValues() {
		return values;
	}

	public ExecutionSpec getExecutionSpec() {
		return executionSpec;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValues(Map<String, Object> values) {
		this.values = values;
	}

	public void setExecutionSpec(ExecutionSpec executionSpec) {
		this.executionSpec = executionSpec;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ExecutionFlowDescriptor)
			return name.equals(((ExecutionFlowDescriptor) obj).getName());
		return false;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		return (path != null && !path.trim().equals("") ? path + "/" : "")
				+ name;
	}

}
