package org.argeo.slc.core.execution;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/** Workaround when execution placedholders needs to be passed. */
public class ExecutionResourcesFactoryBean implements FactoryBean<Resource> {
	private ExecutionResources executionResources;
	private String relativePath;

	public Resource getObject() throws Exception {
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
