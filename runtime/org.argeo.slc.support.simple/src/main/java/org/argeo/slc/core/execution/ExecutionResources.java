package org.argeo.slc.core.execution;

import org.springframework.core.io.Resource;

public interface ExecutionResources {
	public Resource getWritableResource(String relativePath);
}
