package org.argeo.slc.core.deploy;

import java.util.Map;

import org.springframework.core.io.Resource;

public interface ResourceSet {
	/**
	 * List the resources, identified by their relative path. Relative paths
	 * must NOT start with a '/'.
	 */
	public Map<String, Resource> listResources();
}
