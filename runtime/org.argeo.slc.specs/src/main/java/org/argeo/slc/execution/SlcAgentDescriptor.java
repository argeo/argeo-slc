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
import java.util.ArrayList;
import java.util.List;


public class SlcAgentDescriptor implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;
	private String uuid;
	private String host;
	private List<ExecutionModuleDescriptor> moduleDescriptors = new ArrayList<ExecutionModuleDescriptor>();

	public SlcAgentDescriptor() {

	}

	public SlcAgentDescriptor(SlcAgentDescriptor template) {
		uuid = template.uuid;
		host = template.host;
		moduleDescriptors.addAll(template.moduleDescriptors);
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public List<ExecutionModuleDescriptor> getModuleDescriptors() {
		return moduleDescriptors;
	}

	public void setModuleDescriptors(
			List<ExecutionModuleDescriptor> modulesDescriptors) {
		this.moduleDescriptors = modulesDescriptors;
	}

	@Override
	public String toString() {
		return host + " #" + uuid;
	}
}
