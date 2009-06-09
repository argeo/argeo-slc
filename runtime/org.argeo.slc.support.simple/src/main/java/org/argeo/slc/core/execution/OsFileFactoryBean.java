package org.argeo.slc.core.execution;

import java.io.File;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/** Workaround when execution placedholders needs to be passed. */
public class OsFileFactoryBean implements FactoryBean {
	private ExecutionResources executionResources;
	private Resource resource;
	private Boolean overwrite = false;

	/** Return an existing file on the fiel system. */
	public Object getObject() throws Exception {
		Assert.notNull(executionResources, "executionResources is null");
		Assert.notNull(resource, "relativePath is null");
		return executionResources.getAsOsPath(resource, overwrite);
	}

	public Class<? extends Object> getObjectType() {
		return File.class;
	}

	public boolean isSingleton() {
		return true;
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
