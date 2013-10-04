package org.argeo.slc;

/**
 * Adds a dimension to {@link NameVersion} by adding an arbitrary category (e.g.
 * Maven groupId, yum repository ID, etc.)
 */
public interface CategorizedNameVersion extends NameVersion {
	/** The category of the component. */
	public String getCategory();
}
