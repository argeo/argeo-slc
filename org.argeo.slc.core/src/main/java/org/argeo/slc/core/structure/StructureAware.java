package org.argeo.slc.core.structure;

/**
 * Wrapper for an element, which is able to propagate registration to
 * sub-elements.
 */
public interface StructureAware {
	/** Called <b>after</b> registration. */
	public void notifyCurrentPath(StructureRegistry registry, StructurePath path);
}
