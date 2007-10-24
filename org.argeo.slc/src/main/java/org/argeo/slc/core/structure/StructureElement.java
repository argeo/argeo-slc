package org.argeo.slc.core.structure;

/** Atomic element holding the reference to the element which is structured. */
public interface StructureElement {
	/** Path to this element. */
	public StructurePath getPath();

	/** Description of this element. */
	public String getDescription();
}
