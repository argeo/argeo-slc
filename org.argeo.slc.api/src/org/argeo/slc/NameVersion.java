package org.argeo.slc;

/**
 * Abstraction of a name / version pair, typically used as coordinates for a
 * software module either deployed or packaged as an archive.
 */
public interface NameVersion {
	/** The name of the component. */
	public String getName();

	/** The version of the component. */
	public String getVersion();
}
