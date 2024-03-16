package org.argeo.api.slc;

/**
 * Abstraction of a name / version pair, typically used as coordinates for a
 * software module either deployed or packaged as an archive.
 */
public interface NameVersion {
	/** The name of the component. */
	String getName();

	/** The version of the component. */
	String getVersion();

	/**
	 * The forward compatible branch of this version, by default it is
	 * [major].[minor].
	 */
	default String getBranch() {
		String[] parts = getVersion().split("\\.");
		if (parts.length < 2)
			throw new IllegalStateException("Version " + getVersion() + " cannot be interpreted as branch.");
		return parts[0] + "." + parts[1];
	}
}
