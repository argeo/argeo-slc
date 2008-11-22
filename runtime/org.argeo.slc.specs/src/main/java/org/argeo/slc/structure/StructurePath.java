package org.argeo.slc.structure;

/**
 * Path allowing to uniquely identify a <code>StructureElement</code> within a
 * registry.
 * 
 * @see StructureElement
 * @see StructurePath
 */
public interface StructurePath {
	/**
	 * Unique representation as a string. Most implementation will also provide
	 * a mean to interpret this string.
	 */
	public String getAsUniqueString();
}
