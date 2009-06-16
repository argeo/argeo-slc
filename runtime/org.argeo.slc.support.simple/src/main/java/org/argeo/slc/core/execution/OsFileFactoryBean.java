package org.argeo.slc.core.execution;

import java.io.File;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/** Retrieve an OS File from the given resource. */
public class OsFileFactoryBean implements FactoryBean {
	private ExecutionResources executionResources;
	private Resource resource;
	private Boolean overwrite = false;

	/** Return an existing file on the file system. */
	public Object getObject() throws Exception {
		Assert.notNull(executionResources, "executionResources is null");
		Assert.notNull(resource, "resource is null");
		return executionResources.getAsOsPath(resource, overwrite);
	}

	/** Return {@link Object} because CGLIB is unable to proxy {@link File}.*/
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
