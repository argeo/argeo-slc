package org.argeo.slc.core.execution;

import org.springframework.core.io.Resource;

public interface ExecutionResources {
	/** Allocates a local file in the writable area and return it as a resource. */
	public Resource getWritableResource(String relativePath);

	/**
	 * Returns the resource as a file path. If the resource is not writable it
	 * is copied as a file in the writable area and the path to this local file
	 * is returned.
	 */
	public String getAsOsPath(Resource resource, Boolean overwrite);
}
