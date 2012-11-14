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

import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/** Workaround when execution placedholders needs to be passed.*/
public class ExecutionResourcesFactoryBean implements FactoryBean {
	private ExecutionResources executionResources;
	private String relativePath;

	public Object getObject() throws Exception {
		Assert.notNull(executionResources, "executionResources is null");
		Assert.notNull(relativePath, "relativePath is null");
		return executionResources.getWritableResource(relativePath);
	}

	public Class<? extends Object> getObjectType() {
		return Resource.class;
	}

	public boolean isSingleton() {
		return true;
	}

	public void setExecutionResources(ExecutionResources executionResources) {
		this.executionResources = executionResources;
	}

	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}

}
