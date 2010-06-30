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

package org.argeo.slc.execution;

import java.util.Map;

public class ExecutionFlowDescriptor {
	private String name;
	private String description;
	private String path;
	private Map<String, Object> values;
	private ExecutionSpec executionSpec;

	public ExecutionFlowDescriptor() {
	}

	public ExecutionFlowDescriptor(String name, Map<String, Object> values,
			ExecutionSpec executionSpec) {
		this.name = name;
		this.values = values;
		this.executionSpec = executionSpec;
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

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

}
