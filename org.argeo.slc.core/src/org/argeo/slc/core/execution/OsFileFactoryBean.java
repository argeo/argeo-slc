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
package org.argeo.slc.core.execution;

import java.io.File;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/** Retrieve an OS File from the given resource. */
public class OsFileFactoryBean implements FactoryBean<String> {
	private ExecutionResources executionResources;
	private Resource resource;
	private Boolean overwrite = false;

	/** Return an existing file on the file system. */
	public String getObject() throws Exception {
		Assert.notNull(executionResources, "executionResources is null");
		Assert.notNull(resource, "resource is null");
		return executionResources.getAsOsPath(resource, overwrite);
	}

	/** Return {@link Object} because CGLIB is unable to proxy {@link File}. */
	public Class<? extends Object> getObjectType() {
		return CharSequence.class;
	}

	public boolean isSingleton() {
		return false;
	}

	/** The execution resources object. */
	public void setExecutionResources(ExecutionResources executionResources) {
		this.executionResources = executionResources;
	}

	/** The resource to access. */
	public void setResource(Resource resource) {
		this.resource = resource;
	}

	/**
	 * Whether to overwrite the resource if it already exists. Default is
	 * <code>false</code>.
	 */
	public void setOverwrite(Boolean overwrite) {
		this.overwrite = overwrite;
	}

}
