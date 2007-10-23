package org.argeo.slc.core.structure;

public interface StructureAware {
	public StructureElement getElement();
	/** Called <b>after</b> registration.*/
	public void onRegister(StructureRegistry registry);
}
