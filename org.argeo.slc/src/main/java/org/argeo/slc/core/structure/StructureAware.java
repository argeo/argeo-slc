package org.argeo.slc.core.structure;

/**
 * Wrapper for an element, which is able to propagate registration to
 * sub-elements.
 */
public interface StructureAware {
	/** Get the wrapped element.*/
	public StructureElement getElement();

	/** Called <b>after</b> registration. */
	public void onRegister(StructureRegistry registry);
}
