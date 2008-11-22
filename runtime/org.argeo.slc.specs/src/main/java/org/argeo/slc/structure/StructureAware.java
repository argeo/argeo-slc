package org.argeo.slc.structure;

/**
 * Wrapper for an element, which is able to propagate registration to
 * sub-elements.
 */
public interface StructureAware<T extends StructurePath> {
	/** Called <b>after</b> registration. */
	public void notifyCurrentPath(StructureRegistry<T> registry, T path);
}
