package org.argeo.api.slc.execution;

import java.io.File;
import java.nio.file.Path;

/** Provides write access to resources during execution */
public interface ExecutionResources {
	/** The base directory where this execution can write */
	public File getWritableBaseDir();

	/** Allocates a local file in the writable area and return it as a resource. */
	public Path getWritableResource(String relativePath);

	/**
	 * Allocates a local file in the writable area and return it as a fully
	 * qualified OS path.
	 */
	public String getWritableOsPath(String relativePath);

	/**
	 * Allocates a local file in the writable area and return it as a {@link File}.
	 */
	public File getWritableOsFile(String relativePath);

	/**
	 * Returns the resource as a file path. If the resource is not writable it is
	 * copied as a file in the writable area and the path to this local file is
	 * returned.
	 */
	public String getAsOsPath(Path resource, Boolean overwrite);
}
